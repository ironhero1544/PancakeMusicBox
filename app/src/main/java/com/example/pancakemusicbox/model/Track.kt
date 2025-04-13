package com.example.pancakemusicbox.model

/**
 * 트랙/곡 정보를 담는 클래스
 * Java 코드와의 호환성을 위해 필드와 getter 패턴 사용
 */
class Track {
    // 프라이빗 필드
    private val _id: String
    private val _title: String
    private val _artist: String
    private val _albumTitle: String
    private val _albumArtUri: String
    private val _duration: Long           // 밀리초 단위 재생 시간
    private val _filePath: String         // 파일 경로
    private val _audioQuality: AudioQuality
    private val _genre: String?
    private val _year: Int?
    private val _trackNumber: Int?
    private val _composer: String?
    private val _playCount: Int
    private val _lastPlayed: Long?

    // 편의를 위한 필드
    @JvmField
    val isHighRes: Boolean

    constructor(
        id: String,
        title: String,
        artist: String,
        albumTitle: String,
        albumArtUri: String,
        duration: Long,
        filePath: String,
        audioQuality: AudioQuality,
        genre: String? = null,
        year: Int? = null,
        trackNumber: Int? = null,
        composer: String? = null,
        playCount: Int = 0,
        lastPlayed: Long? = null
    ) {
        this._id = id
        this._title = title
        this._artist = artist
        this._albumTitle = albumTitle
        this._albumArtUri = albumArtUri
        this._duration = duration
        this._filePath = filePath
        this._audioQuality = audioQuality
        this._genre = genre
        this._year = year
        this._trackNumber = trackNumber
        this._composer = composer
        this._playCount = playCount
        this._lastPlayed = lastPlayed
        
        // 파생 속성 초기화
        this.isHighRes = audioQuality.isHighResolution
    }

    // Getter 메서드
    fun getId(): String = _id
    fun getTitle(): String = _title
    fun getArtist(): String = _artist
    fun getAlbumTitle(): String = _albumTitle
    fun getAlbumArtUri(): String = _albumArtUri
    fun getDuration(): Long = _duration
    fun getFilePath(): String = _filePath
    fun getAudioQuality(): AudioQuality = _audioQuality
    fun getGenre(): String? = _genre
    fun getYear(): Int? = _year
    fun getTrackNumber(): Int? = _trackNumber
    fun getComposer(): String? = _composer
    fun getPlayCount(): Int = _playCount
    fun getLastPlayed(): Long? = _lastPlayed
    
    // 편의를 위한 메서드
    fun getGenreOrUnknown(): String = _genre ?: "Unknown"

    // 데이터 클래스와 같은 기능 제공을 위한 메서드들
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Track) return false

        return _id == other._id &&
                _title == other._title &&
                _artist == other._artist &&
                _albumTitle == other._albumTitle &&
                _albumArtUri == other._albumArtUri &&
                _duration == other._duration &&
                _filePath == other._filePath &&
                _audioQuality == other._audioQuality &&
                _genre == other._genre &&
                _year == other._year &&
                _trackNumber == other._trackNumber &&
                _composer == other._composer &&
                _playCount == other._playCount &&
                _lastPlayed == other._lastPlayed
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + _title.hashCode()
        result = 31 * result + _artist.hashCode()
        result = 31 * result + _albumTitle.hashCode()
        result = 31 * result + _albumArtUri.hashCode()
        result = 31 * result + _duration.hashCode()
        result = 31 * result + _filePath.hashCode()
        result = 31 * result + _audioQuality.hashCode()
        result = 31 * result + (_genre?.hashCode() ?: 0)
        result = 31 * result + (_year ?: 0)
        result = 31 * result + (_trackNumber ?: 0)
        result = 31 * result + (_composer?.hashCode() ?: 0)
        result = 31 * result + _playCount
        result = 31 * result + (_lastPlayed?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Track(id='$_id', title='$_title', artist='$_artist', albumTitle='$_albumTitle', " +
                "albumArtUri='$_albumArtUri', duration=$_duration, filePath='$_filePath', " +
                "audioQuality=$_audioQuality, genre=$_genre, year=$_year, trackNumber=$_trackNumber, " +
                "composer=$_composer, playCount=$_playCount, lastPlayed=$_lastPlayed)"
    }
}
