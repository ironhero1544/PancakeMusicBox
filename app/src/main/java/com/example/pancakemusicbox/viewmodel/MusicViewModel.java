package com.example.pancakemusicbox.viewmodel;

import android.app.Application;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.pancakemusicbox.model.Album;
import com.example.pancakemusicbox.model.Playlist;
import com.example.pancakemusicbox.model.Track;
import com.example.pancakemusicbox.repository.MusicRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 음악 데이터 및 기능에 대한 ViewModel
 * Repository 패턴을 사용하여 데이터 액세스 및 비즈니스 로직 처리
 */
public class MusicViewModel extends AndroidViewModel {
    // 리포지토리 인스턴스
    private final MusicRepository repository;
    
    // 현재 진행 중인 작업에 대한 로딩 상태
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // 검색 쿼리
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    
    // 검색 결과 트랙
    private final LiveData<List<Track>> searchResultTracks;
    
    // 생성자
    public MusicViewModel(@NonNull Application application) {
        super(application);
        repository = MusicRepository.getInstance(application);
        
        // 검색 기능 설정
        searchResultTracks = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return repository.getAllTracks();
            } else {
                return Transformations.map(repository.getAllTracks(), tracks -> {
                    return filterTracks(tracks, query);
                });
            }
        });
        
        // 자동으로 기본 음악 디렉토리 스캔 시작
        scanDefaultMusicDirs();
    }
    
    // 트랙 필터링 (검색어 기반)
    private List<Track> filterTracks(List<Track> tracks, String query) {
        if (tracks == null) return new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        List<Track> filtered = new ArrayList<>();
        
        for (Track track : tracks) {
            // Kotlin 프로퍼티 접근을 위한 getter 메서드 사용
            if (track.getTitle().toLowerCase().contains(lowerQuery) ||
                track.getArtist().toLowerCase().contains(lowerQuery) ||
                track.getAlbumTitle().toLowerCase().contains(lowerQuery)) {
                // composer는 null 가능성이 있으므로 별도 처리
                String composer = track.getComposer();
                if (composer != null && composer.toLowerCase().contains(lowerQuery)) {
                    filtered.add(track);
                    continue;
                }
                filtered.add(track);
            }
        }
        
        return filtered;
    }
    
    // 기본 음악 디렉토리 스캔
    public void scanDefaultMusicDirs() {
        isLoading.setValue(true);
        repository.scanDefaultDirectories();
    }
    
    // 특정 디렉토리 스캔
    public void scanDirectory(String path) {
        isLoading.setValue(true);
        repository.scanDirectory(path);
    }
    
    // 특정 트랙 재생
    public void playTrack(Track track) {
        repository.playTrack(track);
    }
    
    // 검색어 설정
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
    
    // 새 플레이리스트 생성
    public Playlist createPlaylist(String name, List<Track> tracks) {
        return repository.createPlaylist(name, tracks);
    }
    
    // 플레이리스트에 트랙 추가
    public void addTrackToPlaylist(Playlist playlist, Track track) {
        repository.addTrackToPlaylist(playlist, track);
    }
    
    // LiveData 접근자
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<Track> getCurrentlyPlayingTrack() {
        return repository.getCurrentlyPlayingTrack();
    }
    
    public LiveData<Integer> getScanProgress() {
        return repository.getScanProgress();
    }
    
    public LiveData<List<Track>> getAllTracks() {
        return repository.getAllTracks();
    }
    
    public LiveData<List<Track>> getRecentlyPlayedTracks() {
        return repository.getRecentlyPlayedTracks();
    }
    
    public LiveData<List<Track>> getFrequentlyPlayedTracks() {
        return repository.getFrequentlyPlayedTracks();
    }
    
    public LiveData<List<Track>> getRecentlyAddedTracks() {
        return repository.getRecentlyAddedTracks();
    }
    
    public LiveData<Map<String, List<Track>>> getTracksByGenre() {
        return repository.getTracksByGenre();
    }
    
    public LiveData<List<Playlist>> getPlaylists() {
        return repository.getPlaylists();
    }
    
    public LiveData<List<Track>> getSearchResultTracks() {
        return searchResultTracks;
    }
    
    public List<Album> getAlbums() {
        return repository.getAlbums();
    }
    
    public Playlist getPlaylistById(String id) {
        return repository.getPlaylistById(id);
    }
}