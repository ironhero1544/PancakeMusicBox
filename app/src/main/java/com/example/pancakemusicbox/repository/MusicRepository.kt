package com.example.pancakemusicbox.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pancakemusicbox.audio.AudioScannerNative
import com.example.pancakemusicbox.model.Album
import com.example.pancakemusicbox.model.Playlist
import com.example.pancakemusicbox.model.Track
import java.io.File
import java.util.UUID

/**
 * 음악 파일과 관련 메타데이터를 관리하는 리포지토리
 * 앱 내에서 음악 데이터에 접근하기 위한 중앙 인터페이스 역할
 */
class MusicRepository private constructor(private val context: Context) {
    companion object {
        private const val TAG = "MusicRepository"
        private const val DB_FILENAME = "music_database.db"

        // 싱글톤 인스턴스
        @Volatile
        private var instance: MusicRepository? = null

        @JvmStatic
        fun getInstance(context: Context): MusicRepository {
            return instance ?: synchronized(this) {
                instance ?: MusicRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    // 네이티브 스캐너 인스턴스
    private val scanner = AudioScannerNative.getInstance()

    // 현재 재생 중인 트랙
    private val currentlyPlayingTrack = MutableLiveData<Track>()

    // 스캔 진행률
    private val scanProgress = MutableLiveData<Int>(0)

    // 모든 트랙 목록
    private val allTracks = MutableLiveData<List<Track>>(emptyList())

    // 최근 재생 트랙 목록
    private val recentlyPlayedTracks = MutableLiveData<List<Track>>(emptyList())

    // 자주 재생되는 트랙 목록
    private val frequentlyPlayedTracks = MutableLiveData<List<Track>>(emptyList())

    // 최근 추가된 트랙 목록
    private val recentlyAddedTracks = MutableLiveData<List<Track>>(emptyList())

    // 장르별 트랙 목록
    private val tracksByGenre = MutableLiveData<Map<String, List<Track>>>(emptyMap())

    // 플레이리스트 목록
    private val playlists = MutableLiveData<List<Playlist>>(emptyList())

    // 사용자 생성 플레이리스트
    private val userPlaylists = mutableListOf<Playlist>()

    init {
        // 스캔 진행 리스너 설정
        scanner.setProgressListener { current, total ->
            val progressPercent = (current / total.toFloat() * 100).toInt()
            scanProgress.postValue(progressPercent)
        }

        // 데이터베이스 로드
        loadDatabase()
    }

    // 데이터베이스 로드
    private fun loadDatabase() {
        val dbFile = File(context.filesDir, DB_FILENAME)
        
        if (dbFile.exists()) {
            val success = scanner.loadDatabase(dbFile.absolutePath)
            if (success) {
                Log.d(TAG, "Database loaded successfully")
                refreshData()
            } else {
                Log.e(TAG, "Failed to load database")
            }
        } else {
            Log.d(TAG, "No database file found, will scan music directories later")
        }
    }

    // 데이터베이스 저장
    private fun saveDatabase() {
        val dbFile = File(context.filesDir, DB_FILENAME)
        val success = scanner.saveDatabase(dbFile.absolutePath)
        
        if (success) {
            Log.d(TAG, "Database saved successfully")
        } else {
            Log.e(TAG, "Failed to save database")
        }
    }

    // 데이터 새로고침
    private fun refreshData() {
        // 모든 트랙 목록 업데이트
        allTracks.postValue(scanner.getAllTracks())
        
        // 최근 재생 트랙 업데이트
        recentlyPlayedTracks.postValue(scanner.getRecentlyPlayedTracks(20))
        
        // 자주 재생되는 트랙 업데이트
        frequentlyPlayedTracks.postValue(scanner.getFrequentlyPlayedTracks(20))
        
        // 최근 추가된 트랙 업데이트
        recentlyAddedTracks.postValue(scanner.getRecentlyAddedTracks(20))
        
        // 장르별 트랙 업데이트
        tracksByGenre.postValue(scanner.getTracksByGenre())
        
        // 현재 재생 중인 트랙이 없으면 첫 번째 트랙으로 설정
        if (currentlyPlayingTrack.value == null) {
            val tracks = recentlyPlayedTracks.value
            if (!tracks.isNullOrEmpty()) {
                currentlyPlayingTrack.postValue(tracks[0])
            }
        }
        
        // 생성된 플레이리스트 업데이트 및 제공
        updateGeneratedPlaylists()
    }

    // 기본 폴더들 스캔
    fun scanDefaultDirectories() {
        Thread {
            try {
                Log.d(TAG, "Starting scan of default directories")
                
                // 외부 저장소의 음악 디렉토리
                val externalMusicDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC).absolutePath
                Log.d(TAG, "Scanning music directory: $externalMusicDir")
                
                val musicSuccess = scanner.scanDirectory(externalMusicDir)
                if (!musicSuccess) {
                    Log.e(TAG, "Failed to scan music directory: $externalMusicDir")
                }
                
                // 다운로드 디렉토리도 확인
                val downloadDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).absolutePath
                Log.d(TAG, "Scanning download directory: $downloadDir")
                
                val downloadSuccess = scanner.scanDirectory(downloadDir)
                if (!downloadSuccess) {
                    Log.e(TAG, "Failed to scan download directory: $downloadDir")
                }
                
                // 내부 저장소의 Music 폴더도 스캔 (더 나은 접근 가능성)
                try {
                    val internalMusicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath
                    if (internalMusicDir != null) {
                        Log.d(TAG, "Scanning internal music directory: $internalMusicDir")
                        scanner.scanDirectory(internalMusicDir)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error scanning internal music directory", e)
                }
                
                // 스캔 완료 후 데이터베이스 저장 및 데이터 새로고침
                saveDatabase()
                refreshData()
                Log.d(TAG, "Scan completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error during music directory scanning", e)
            }
        }.start()
    }

    // 특정 디렉토리 스캔
    fun scanDirectory(directoryPath: String) {
        Thread {
            val success = scanner.scanDirectory(directoryPath)
            if (success) {
                saveDatabase()
                refreshData()
            }
        }.start()
    }

    // 특정 파일 스캔
    fun scanFile(filePath: String) {
        Thread {
            val success = scanner.scanFile(filePath)
            if (success) {
                saveDatabase()
                refreshData()
            }
        }.start()
    }

    // 트랙 재생
    fun playTrack(track: Track) {
        // 현재 재생 중인 트랙 업데이트
        currentlyPlayingTrack.postValue(track)
        
        // 재생 횟수 및 마지막 재생 시간 업데이트
        Thread {
            scanner.updatePlayCount(track.getId())
            scanner.updateLastPlayed(track.getId())
            saveDatabase()
            
            // 재생 트랙 목록 새로고침
            recentlyPlayedTracks.postValue(scanner.getRecentlyPlayedTracks(20))
            frequentlyPlayedTracks.postValue(scanner.getFrequentlyPlayedTracks(20))
        }.start()
    }

    // 플레이리스트 생성
    fun createPlaylist(name: String, tracks: List<Track>): Playlist {
        val playlist = Playlist(
                id = UUID.randomUUID().toString(),
                name = name,
                tracks = ArrayList(tracks),
                createdAt = System.currentTimeMillis(),
                isAutoGenerated = false
        )
        
        userPlaylists.add(playlist)
        
        // 플레이리스트 목록 업데이트
        val currentPlaylists = ArrayList(userPlaylists)
        playlists.postValue(currentPlaylists)
        
        return playlist
    }

    // 플레이리스트에 트랙 추가
    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        for (p in userPlaylists) {
            if (p.getId() == playlist.getId()) {
                val tracksList = p.getTracks().toMutableList()
                tracksList.add(track)
                
                // 새 플레이리스트 인스턴스 생성 (immutable 객체 대신)
                val updatedPlaylist = Playlist(
                    id = p.getId(),
                    name = p.getName(),
                    tracks = tracksList,
                    createdAt = p.getCreatedAt(),
                    isAutoGenerated = p.isAutoGenerated()
                )
                
                userPlaylists.remove(p)
                userPlaylists.add(updatedPlaylist)
                break
            }
        }
        
        // 플레이리스트 목록 업데이트
        val currentPlaylists = ArrayList(userPlaylists)
        playlists.postValue(currentPlaylists)
    }

    // 장르별 플레이리스트 자동 생성
    private fun updateGeneratedPlaylists() {
        val genreTracks = tracksByGenre.value ?: return
        if (genreTracks.isEmpty()) return
        
        val generatedPlaylists = mutableListOf<Playlist>()
        
        // 각 장르별 플레이리스트 생성
        for ((genre, tracks) in genreTracks) {
            if (tracks.size >= 3) { // 최소 3곡 이상인 경우만 플레이리스트 생성
                val playlist = Playlist(
                        id = "genre_${genre.toLowerCase().replace(" ", "_")}",
                        name = "Best of $genre",
                        tracks = tracks.toMutableList(),
                        createdAt = System.currentTimeMillis(),
                        isAutoGenerated = true // 자동 생성 플레이리스트
                )
                generatedPlaylists.add(playlist)
            }
        }
        
        // 최근 재생 플레이리스트 생성
        val recentTracks = recentlyPlayedTracks.value
        if (!recentTracks.isNullOrEmpty()) {
            val recentPlaylist = Playlist(
                    id = "recently_played",
                    name = "Recently Played",
                    tracks = recentTracks.toMutableList(),
                    createdAt = System.currentTimeMillis(),
                    isAutoGenerated = true
            )
            generatedPlaylists.add(recentPlaylist)
        }
        
        // 자주 재생 플레이리스트 생성
        val frequentTracks = frequentlyPlayedTracks.value
        if (!frequentTracks.isNullOrEmpty()) {
            val frequentPlaylist = Playlist(
                    id = "frequently_played",
                    name = "My Favorites",
                    tracks = frequentTracks.toMutableList(),
                    createdAt = System.currentTimeMillis(),
                    isAutoGenerated = true
            )
            generatedPlaylists.add(frequentPlaylist)
        }
        
        // 최종 플레이리스트 목록 (자동 생성 + 사용자 생성)
        val allPlaylists = mutableListOf<Playlist>()
        allPlaylists.addAll(generatedPlaylists)
        allPlaylists.addAll(userPlaylists)
        
        playlists.postValue(allPlaylists)
    }

    // LiveData 접근자
    fun getCurrentlyPlayingTrack(): LiveData<Track> = currentlyPlayingTrack
    fun getScanProgress(): LiveData<Int> = scanProgress
    fun getAllTracks(): LiveData<List<Track>> = allTracks
    fun getRecentlyPlayedTracks(): LiveData<List<Track>> = recentlyPlayedTracks
    fun getFrequentlyPlayedTracks(): LiveData<List<Track>> = frequentlyPlayedTracks
    fun getRecentlyAddedTracks(): LiveData<List<Track>> = recentlyAddedTracks
    fun getTracksByGenre(): LiveData<Map<String, List<Track>>> = tracksByGenre
    fun getPlaylists(): LiveData<List<Playlist>> = playlists

    // ID로 플레이리스트 찾기
    fun getPlaylistById(id: String): Playlist? {
        return playlists.value?.find { it.getId() == id }
    }

    // 앨범 가져오기
    fun getAlbums(): List<Album> {
        val tracks = allTracks.value ?: return emptyList()
        if (tracks.isEmpty()) return emptyList()
        
        // 앨범별로 트랙 그룹화
        val albumTracks = tracks.groupBy { "${it.getAlbumTitle()}|${it.getArtist()}" }
        
        // 앨범 객체 생성
        return albumTracks.map { (_, albumTrackList) ->
            val firstTrack = albumTrackList.first()
            
            Album(
                    id = UUID.randomUUID().toString(),
                    title = firstTrack.getAlbumTitle(),
                    artist = firstTrack.getArtist(),
                    artworkUri = firstTrack.getAlbumArtUri(),
                    year = firstTrack.getYear(),
                    tracks = albumTrackList,
                    genre = firstTrack.getGenre()
            )
        }
    }
}
