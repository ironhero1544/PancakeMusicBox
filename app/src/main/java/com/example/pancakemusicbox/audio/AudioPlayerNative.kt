package com.example.pancakemusicbox.audio

/**
 * Oboe 기반 네이티브 오디오 엔진에 대한 JNI 인터페이스
 */
class AudioPlayerNative private constructor() {
    
    companion object {
        // 라이브러리 로드 성공 여부
        private var nativeLibraryLoaded = false
        
        // 싱글톤 인스턴스
        private var instance: AudioPlayerNative? = null
        
        init {
            try {
                System.loadLibrary("pancakemusicbox")
                nativeLibraryLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
                nativeLibraryLoaded = false
            }
        }

        /**
         * 싱글톤 인스턴스 가져오기
         */
        @Synchronized
        fun getInstance(): AudioPlayerNative {
            if (instance == null) {
                instance = AudioPlayerNative()
            }
            return instance!!
        }
        
        /**
         * 네이티브 라이브러리 로드 여부 확인
         */
        fun isNativeLibraryLoaded(): Boolean {
            return nativeLibraryLoaded
        }
    }

    // 네이티브 메소드들

    /**
     * 오디오 파일 로드
     * @param filePath 오디오 파일 경로
     * @return 로드 성공 여부
     */
    fun loadFile(filePath: String): Boolean {
        return if (nativeLibraryLoaded) {
            nativeLoadFile(filePath)
        } else {
            false
        }
    }
    
    private external fun nativeLoadFile(filePath: String): Boolean

    /**
     * 재생 시작
     */
    fun play() {
        if (nativeLibraryLoaded) {
            nativePlay()
        }
    }
    
    private external fun nativePlay()

    /**
     * 일시 정지
     */
    fun pause() {
        if (nativeLibraryLoaded) {
            nativePause()
        }
    }
    
    private external fun nativePause()

    /**
     * 재생 중지
     */
    fun stop() {
        if (nativeLibraryLoaded) {
            nativeStop()
        }
    }
    
    private external fun nativeStop()

    /**
     * 지정된 위치로 이동
     * @param positionMs 밀리초 단위 위치
     */
    fun seekTo(positionMs: Long) {
        if (nativeLibraryLoaded) {
            nativeSeekTo(positionMs)
        }
    }
    
    private external fun nativeSeekTo(positionMs: Long)

    /**
     * 현재 재생 중인지 확인
     * @return 재생 중이면 true
     */
    fun isPlaying(): Boolean {
        return if (nativeLibraryLoaded) {
            nativeIsPlaying()
        } else {
            false
        }
    }
    
    private external fun nativeIsPlaying(): Boolean

    /**
     * 현재 재생 위치 가져오기
     * @return 밀리초 단위 현재 위치
     */
    fun getCurrentPosition(): Long {
        return if (nativeLibraryLoaded) {
            nativeGetCurrentPosition()
        } else {
            0L
        }
    }
    
    private external fun nativeGetCurrentPosition(): Long

    /**
     * 트랙 전체 길이 가져오기
     * @return 밀리초 단위 전체 길이
     */
    fun getDuration(): Long {
        return if (nativeLibraryLoaded) {
            nativeGetDuration()
        } else {
            0L
        }
    }
    
    private external fun nativeGetDuration(): Long

    /**
     * 샘플링 레이트 설정
     * @param sampleRate 샘플링 레이트 (Hz)
     */
    fun setSampleRate(sampleRate: Int) {
        if (nativeLibraryLoaded) {
            nativeSetSampleRate(sampleRate)
        }
    }
    
    private external fun nativeSetSampleRate(sampleRate: Int)

    /**
     * 비트 뎁스 설정
     * @param bitDepth 비트 뎁스 (16, 24, 32)
     */
    fun setBitDepth(bitDepth: Int) {
        if (nativeLibraryLoaded) {
            nativeSetBitDepth(bitDepth)
        }
    }
    
    private external fun nativeSetBitDepth(bitDepth: Int)

    /**
     * 채널 수 설정
     * @param channelCount 채널 수 (1 또는 2)
     */
    fun setChannelCount(channelCount: Int) {
        if (nativeLibraryLoaded) {
            nativeSetChannelCount(channelCount)
        }
    }
    
    private external fun nativeSetChannelCount(channelCount: Int)

    /**
     * 볼륨 설정
     * @param volume 볼륨 (0.0 ~ 1.0)
     */
    fun setVolume(volume: Float) {
        if (nativeLibraryLoaded) {
            nativeSetVolume(volume)
        }
    }
    
    private external fun nativeSetVolume(volume: Float)

    /**
     * EQ 활성화/비활성화
     * @param enable 활성화 여부
     */
    fun enableEQ(enable: Boolean) {
        if (nativeLibraryLoaded) {
            nativeEnableEQ(enable)
        }
    }
    
    private external fun nativeEnableEQ(enable: Boolean)

    /**
     * EQ 밴드 게인 설정
     * @param band 밴드 인덱스 (0-9)
     * @param gain 게인 값 (dB)
     */
    fun setEQBand(band: Int, gain: Float) {
        if (nativeLibraryLoaded) {
            nativeSetEQBand(band, gain)
        }
    }
    
    private external fun nativeSetEQBand(band: Int, gain: Float)

    /**
     * 볼륨 정규화 활성화/비활성화
     * @param enable 활성화 여부
     */
    fun enableVolumeNormalization(enable: Boolean) {
        if (nativeLibraryLoaded) {
            nativeEnableVolumeNormalization(enable)
        }
    }
    
    private external fun nativeEnableVolumeNormalization(enable: Boolean)

    /**
     * 타겟 LUFS 값 설정
     * @param lufsValue LUFS 값 (일반적으로 -23 ~ -9)
     */
    fun setTargetLUFS(lufsValue: Float) {
        if (nativeLibraryLoaded) {
            nativeSetTargetLUFS(lufsValue)
        }
    }
    
    private external fun nativeSetTargetLUFS(lufsValue: Float)

    /**
     * 하드웨어에 맞게 오디오 엔진 최적화
     * @param useHeadphones 헤드폰 사용 여부
     * @param isHighPerformanceDevice 고성능 기기 여부
     */
    fun optimizeForDevice(useHeadphones: Boolean, isHighPerformanceDevice: Boolean) {
        if (nativeLibraryLoaded) {
            nativeOptimizeForDevice(useHeadphones, isHighPerformanceDevice)
        }
    }
    
    private external fun nativeOptimizeForDevice(useHeadphones: Boolean, isHighPerformanceDevice: Boolean)

    /**
     * 시각화 데이터 가져오기
     * @return 시각화 데이터 배열 (0.0 ~ 1.0 범위의 값들)
     */
    fun getVisualizationData(): FloatArray {
        return if (nativeLibraryLoaded) {
            nativeGetVisualizationData()
        } else {
            FloatArray(20) { 0f }
        }
    }
    
    private external fun nativeGetVisualizationData(): FloatArray
}