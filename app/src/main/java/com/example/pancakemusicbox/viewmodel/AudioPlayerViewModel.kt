package com.example.pancakemusicbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pancakemusicbox.audio.AudioPlayerManager
import com.example.pancakemusicbox.model.Track
import kotlinx.coroutines.launch
import java.io.File

/**
 * 오디오 플레이어 ViewModel
 * 오디오 재생 및 플레이리스트 관리를 위한 비즈니스 로직
 */
class AudioPlayerViewModel : ViewModel() {
    // 오디오 플레이어 매니저
    private val playerManager = AudioPlayerManager()
    
    // 플레이어 상태 접근자
    val isPlaying: LiveData<Boolean> = playerManager.isPlaying
    val currentPosition: LiveData<Long> = playerManager.currentPosition
    val duration: LiveData<Long> = playerManager.duration
    val visualizationData: LiveData<FloatArray> = playerManager.visualizationData
    
    // 플레이리스트 및 현재 트랙 상태
    private val _playlist = MutableLiveData<List<Track>>(emptyList())
    val playlist: LiveData<List<Track>> = _playlist
    
    private val _currentTrackIndex = MutableLiveData<Int>(-1)
    val currentTrackIndex: LiveData<Int> = _currentTrackIndex
    
    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> = _currentTrack
    
    // 재생 모드 설정
    private val _isShuffleEnabled = MutableLiveData<Boolean>(false)
    val isShuffleEnabled: LiveData<Boolean> = _isShuffleEnabled
    
    private val _repeatMode = MutableLiveData<Int>(0) // 0: off, 1: all, 2: one
    val repeatMode: LiveData<Int> = _repeatMode
    
    // 오디오 설정
    private val _isEQEnabled = MutableLiveData<Boolean>(false)
    val isEQEnabled: LiveData<Boolean> = _isEQEnabled
    
    private val _isVolumeNormalizationEnabled = MutableLiveData<Boolean>(false)
    val isVolumeNormalizationEnabled: LiveData<Boolean> = _isVolumeNormalizationEnabled
    
    /**
     * 플레이리스트 설정
     * @param tracks 트랙 리스트
     * @param initialIndex 시작 인덱스 (기본값 0)
     */
    fun setPlaylist(tracks: List<Track>, initialIndex: Int = 0) {
        _playlist.value = tracks
        if (tracks.isNotEmpty() && initialIndex in tracks.indices) {
            _currentTrackIndex.value = initialIndex
            _currentTrack.value = tracks[initialIndex]
        }
    }
    
    /**
     * 특정 트랙 로드 및 재생
     * @param index 플레이리스트 내 트랙 인덱스
     * @param autoPlay 자동 재생 여부
     */
    fun loadTrack(index: Int, autoPlay: Boolean = true) {
        val playlist = _playlist.value ?: return
        if (index !in playlist.indices) return
        
        val track = playlist[index]
        _currentTrackIndex.value = index
        _currentTrack.value = track
        
        // 파일 경로 확인
        val filePath = track.getFilePath()
        if (!File(filePath).exists()) {
            // 파일이 존재하지 않을 경우 처리
            return
        }
        
        // 네이티브 플레이어로 로드
        viewModelScope.launch {
            val success = playerManager.loadTrack(filePath)
            if (success && autoPlay) {
                playerManager.play()
            }
        }
    }
    
    /**
     * 현재 트랙 재생/일시정지 토글
     */
    fun togglePlayback() {
        playerManager.togglePlayback()
    }
    
    /**
     * 다음 트랙으로 이동
     */
    fun playNext() {
        val currentIndex = _currentTrackIndex.value ?: return
        val playlist = _playlist.value ?: return
        
        if (playlist.isEmpty()) return
        
        // 반복 모드에 따른 처리
        var nextIndex = currentIndex + 1
        if (nextIndex >= playlist.size) {
            nextIndex = if (_repeatMode.value == 1) 0 else return
        }
        
        loadTrack(nextIndex)
    }
    
    /**
     * 이전 트랙으로 이동
     */
    fun playPrevious() {
        val currentIndex = _currentTrackIndex.value ?: return
        val playlist = _playlist.value ?: return
        
        if (playlist.isEmpty()) return
        
        // 현재 위치가 3초 이상이면 현재 곡 처음으로 이동
        if ((playerManager.currentPosition.value ?: 0) > 3000) {
            playerManager.seekTo(0)
            return
        }
        
        // 이전 트랙으로 이동
        var prevIndex = currentIndex - 1
        if (prevIndex < 0) {
            prevIndex = if (_repeatMode.value == 1) playlist.size - 1 else return
        }
        
        loadTrack(prevIndex)
    }
    
    /**
     * 재생 위치 이동
     * @param positionMs 밀리초 단위 위치
     */
    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }
    
    /**
     * 셔플 모드 토글
     */
    fun toggleShuffle() {
        _isShuffleEnabled.value = !(_isShuffleEnabled.value ?: false)
        
        // 셔플 활성화 시 플레이리스트 셔플
        if (_isShuffleEnabled.value == true) {
            // 현재 트랙을 제외한 나머지 셔플
            val currentTrack = _currentTrack.value
            val currentList = _playlist.value ?: return
            
            val shuffledList = if (currentTrack != null) {
                val remaining = currentList.filter { it.getId() != currentTrack.getId() }.shuffled()
                listOf(currentTrack) + remaining
            } else {
                currentList.shuffled()
            }
            
            _playlist.value = shuffledList
            _currentTrackIndex.value = 0
        }
    }
    
    /**
     * 반복 모드 변경
     * 0: 반복 없음, 1: 전체 반복, 2: 한 곡 반복
     */
    fun toggleRepeatMode() {
        val current = _repeatMode.value ?: 0
        _repeatMode.value = (current + 1) % 3
    }
    
    /**
     * EQ 활성화/비활성화 토글
     */
    fun toggleEQ() {
        val newValue = !(_isEQEnabled.value ?: false)
        _isEQEnabled.value = newValue
        playerManager.enableEQ(newValue)
    }
    
    /**
     * EQ 밴드 게인 설정
     * @param band 밴드 인덱스 (0-9)
     * @param gain 게인 값 (dB)
     */
    fun setEQBand(band: Int, gain: Float) {
        playerManager.setEQBand(band, gain)
    }
    
    /**
     * 볼륨 정규화 활성화/비활성화 토글
     */
    fun toggleVolumeNormalization() {
        val newValue = !(_isVolumeNormalizationEnabled.value ?: false)
        _isVolumeNormalizationEnabled.value = newValue
        playerManager.enableVolumeNormalization(newValue)
    }
    
    /**
     * 오디오 품질 설정
     * @param sampleRate 샘플링 레이트 (Hz)
     * @param bitDepth 비트 뎁스 (16, 24, 32)
     * @param channelCount 채널 수 (1 또는 2)
     */
    fun setAudioQuality(sampleRate: Int, bitDepth: Int, channelCount: Int) {
        playerManager.setAudioQuality(sampleRate, bitDepth, channelCount)
    }
    
    /**
     * 타겟 LUFS 값 설정
     * @param lufsValue LUFS 값
     */
    fun setTargetLufs(lufsValue: Int) {
        playerManager.setTargetLUFS(lufsValue.toFloat())
    }
    
    /**
     * 트랙 재생 완료 처리
     * PlayerScreen에서 호출
     */
    fun onTrackCompletion() {
        when (_repeatMode.value) {
            0 -> playNext() // 반복 없음: 다음 트랙
            1 -> playNext() // 전체 반복: 다음 트랙 (마지막이면 처음으로)
            2 -> { // 한 곡 반복: 현재 트랙 다시 시작
                val currentIndex = _currentTrackIndex.value ?: return
                loadTrack(currentIndex)
            }
        }
    }
    
    /**
     * 플레이리스트 내 트랙 이동
     * @param fromIndex 원래 위치
     * @param toIndex 대상 위치
     */
    fun moveTrack(fromIndex: Int, toIndex: Int) {
        val playlist = _playlist.value?.toMutableList() ?: return
        if (fromIndex !in playlist.indices || toIndex !in playlist.indices) return
        
        // 트랙 이동
        val track = playlist.removeAt(fromIndex)
        playlist.add(toIndex, track)
        _playlist.value = playlist
        
        // 현재 트랙 인덱스 조정
        val currentIndex = _currentTrackIndex.value ?: return
        _currentTrackIndex.value = when {
            currentIndex == fromIndex -> toIndex
            fromIndex < currentIndex && currentIndex <= toIndex -> currentIndex - 1
            toIndex <= currentIndex && currentIndex < fromIndex -> currentIndex + 1
            else -> currentIndex
        }
    }
    
    /**
     * 현재 재생목록을 플레이리스트로 저장
     * @param name 플레이리스트 이름
     * @return 저장 성공 여부
     */
    fun saveCurrentPlaylistAs(name: String): Boolean {
        // 실제 구현에서는 Room 데이터베이스에 저장
        // 여기서는 간단한 구현을 위해 항상 성공 반환
        return true
    }
    
    /**
     * 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}