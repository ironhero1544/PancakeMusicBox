package com.example.pancakemusicbox.audio

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * 네이티브 오디오 엔진을 위한 자바 인터페이스
 * Oboe 기반 오디오 플레이어를 사용하기 위한 래퍼 클래스
 */
class AudioPlayerManager {
    // 네이티브 플레이어 인스턴스
    private val nativePlayer = AudioPlayerNative.getInstance()
    
    // 현재 로드된 트랙 정보
    private var currentTrackPath: String? = null
    
    // 플레이어 상태 데이터
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    
    private val _currentPosition = MutableLiveData<Long>(0)
    val currentPosition: LiveData<Long> = _currentPosition
    
    private val _duration = MutableLiveData<Long>(0)
    val duration: LiveData<Long> = _duration
    
    private val _visualizationData = MutableLiveData<FloatArray>()
    val visualizationData: LiveData<FloatArray> = _visualizationData
    
    // 위치 및 시각화 업데이트를 위한 코루틴
    private var updateJob: Job? = null
    private val updateScope = CoroutineScope(Dispatchers.Main)
    
    // 오디오 품질 설정
    private var _sampleRate = 44100
    val sampleRate: Int get() = _sampleRate
    
    private var _bitDepth = 16
    val bitDepth: Int get() = _bitDepth
    
    private var _channelCount = 2
    val channelCount: Int get() = _channelCount
    
    // 생성자
    init {
        // 초기 하드웨어 최적화
        optimizeForCurrentDevice()
    }
    
    /**
     * 오디오 파일 로드
     * @param filePath 오디오 파일 경로
     * @return 로드 성공 여부
     */
    fun loadTrack(filePath: String): Boolean {
        // 현재 재생 중이라면 중지
        stop()
        
        try {
            // 파일 존재 확인
            val file = File(filePath)
            if (!file.exists()) {
                return false
            }
            
            // 네이티브 라이브러리 로드 확인
            if (!AudioPlayerNative.isNativeLibraryLoaded()) {
                return false
            }
            
            // 파일 로드
            val success = nativePlayer.loadFile(filePath)
            if (success) {
                currentTrackPath = filePath
                _duration.value = nativePlayer.getDuration()
                _currentPosition.value = 0
                startUpdates()
            }
            
            return success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * 재생 시작
     */
    fun play() {
        if (currentTrackPath == null) return
        
        nativePlayer.play()
        _isPlaying.value = true
        startUpdates()
    }
    
    /**
     * 일시 정지
     */
    fun pause() {
        nativePlayer.pause()
        _isPlaying.value = false
    }
    
    /**
     * 재생 중지
     */
    fun stop() {
        nativePlayer.stop()
        _isPlaying.value = false
        _currentPosition.value = 0
        stopUpdates()
    }
    
    /**
     * 재생/일시정지 토글
     */
    fun togglePlayback() {
        if (_isPlaying.value == true) {
            pause()
        } else {
            play()
        }
    }
    
    /**
     * 지정된 위치로 이동
     * @param positionMs 밀리초 단위 위치
     */
    fun seekTo(positionMs: Long) {
        nativePlayer.seekTo(positionMs)
        _currentPosition.value = positionMs
    }
    
    /**
     * 오디오 품질 설정
     * @param sampleRate 샘플링 레이트 (Hz)
     * @param bitDepth 비트 뎁스 (16, 24, 32)
     * @param channelCount 채널 수 (1 또는 2)
     */
    fun setAudioQuality(sampleRate: Int, bitDepth: Int, channelCount: Int) {
        _sampleRate = sampleRate
        _bitDepth = bitDepth
        _channelCount = channelCount
        
        nativePlayer.setSampleRate(sampleRate)
        nativePlayer.setBitDepth(bitDepth)
        nativePlayer.setChannelCount(channelCount)
    }
    
    /**
     * 볼륨 설정
     * @param volume 볼륨 (0.0 ~ 1.0)
     */
    fun setVolume(volume: Float) {
        nativePlayer.setVolume(volume)
    }
    
    /**
     * EQ 활성화/비활성화
     * @param enable 활성화 여부
     */
    fun enableEQ(enable: Boolean) {
        nativePlayer.enableEQ(enable)
    }
    
    /**
     * EQ 밴드 게인 설정
     * @param band 밴드 인덱스 (0-9)
     * @param gain 게인 값 (dB)
     */
    fun setEQBand(band: Int, gain: Float) {
        nativePlayer.setEQBand(band, gain)
    }
    
    /**
     * 볼륨 정규화 활성화/비활성화
     * @param enable 활성화 여부
     */
    fun enableVolumeNormalization(enable: Boolean) {
        nativePlayer.enableVolumeNormalization(enable)
    }
    
    /**
     * 타겟 LUFS 값 설정
     * @param lufsValue LUFS 값 (일반적으로 -23 ~ -9)
     */
    fun setTargetLUFS(lufsValue: Float) {
        nativePlayer.setTargetLUFS(lufsValue)
    }
    
    /**
     * 하드웨어에 맞게 오디오 엔진 최적화
     */
    fun optimizeForCurrentDevice() {
        // 여기서 실제로는 연결된 오디오 장치 타입 (헤드폰, 스피커 등) 및
        // 기기 성능 정보를 감지하여 최적화 설정을 해야 함
        // 현재는 기본값으로 처리
        nativePlayer.optimizeForDevice(false, true)
    }
    
    /**
     * 재생 위치 및 시각화 데이터 업데이트 시작
     */
    private fun startUpdates() {
        stopUpdates()
        
        updateJob = updateScope.launch {
            while (isActive) {
                if (_isPlaying.value == true) {
                    // 현재 위치 업데이트
                    _currentPosition.value = nativePlayer.getCurrentPosition()
                    
                    // 시각화 데이터 업데이트
                    _visualizationData.value = nativePlayer.getVisualizationData()
                }
                delay(50) // 50ms 마다 업데이트 (약 20fps)
            }
        }
    }
    
    /**
     * 업데이트 중지
     */
    private fun stopUpdates() {
        updateJob?.cancel()
        updateJob = null
    }
    
    /**
     * 리소스 해제
     */
    fun release() {
        stop()
        stopUpdates()
    }
}
