package com.example.pancakemusicbox.model

/**
 * 오디오 품질 정보를 담는 클래스
 * Java 코드와의 호환성을 위해 필드와 getter 패턴 사용
 */
class AudioQuality {
    private val _sampleRate: Int  // Hz (44100, 48000, 96000, 192000 등)
    private val _bitDepth: Int    // 비트 (16, 24, 32 등)
    private val _format: String   // 포맷 (FLAC, WAV, MP3, AAC 등)
    private val _channels: Int
    private val _bitrate: Int?    // kbps (비손실 포맷이 아닌 경우)

    constructor(
        sampleRate: Int,
        bitDepth: Int,
        format: String,
        channels: Int = 2,
        bitrate: Int? = null
    ) {
        this._sampleRate = sampleRate
        this._bitDepth = bitDepth
        this._format = format
        this._channels = channels
        this._bitrate = bitrate
    }

    // Getter 메서드
    fun getSampleRate(): Int = _sampleRate
    fun getBitDepth(): Int = _bitDepth
    fun getFormat(): String = _format
    fun getChannels(): Int = _channels
    fun getBitrate(): Int? = _bitrate

    // 편의 프로퍼티 및 메서드
    val isHighResolution: Boolean
        get() = _sampleRate > 44100 || _bitDepth > 16

    val isLossless: Boolean
        get() = _format.equals("FLAC", ignoreCase = true) || 
                _format.equals("WAV", ignoreCase = true) ||
                _format.equals("ALAC", ignoreCase = true) ||
                _format.equals("DSD", ignoreCase = true) ||
                _format.equals("MQA", ignoreCase = true)

    fun getAudioQualityString(): String {
        return if (isLossless) {
            "${_bitDepth}bit/${_sampleRate/1000}kHz | $_format"
        } else {
            "$_format | ${_bitrate}kbps"
        }
    }

    // 데이터 클래스와 같은 기능 제공을 위한 메서드들
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioQuality) return false

        return _sampleRate == other._sampleRate &&
                _bitDepth == other._bitDepth &&
                _format == other._format &&
                _channels == other._channels &&
                _bitrate == other._bitrate
    }

    override fun hashCode(): Int {
        var result = _sampleRate
        result = 31 * result + _bitDepth
        result = 31 * result + _format.hashCode()
        result = 31 * result + _channels
        result = 31 * result + (_bitrate ?: 0)
        return result
    }

    override fun toString(): String {
        return "AudioQuality(sampleRate=$_sampleRate, bitDepth=$_bitDepth, " +
                "format='$_format', channels=$_channels, bitrate=$_bitrate)"
    }
}