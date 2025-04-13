#include "include/AudioScanner.h"
#include <algorithm>
#include <filesystem>
#include <fstream>
#include <random>
#include <sstream>
#include <android/log.h>
#include <sys/stat.h>  // stat() 함수 사용을 위한 헤더

#define LOG_TAG "AudioScanner"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace fs = std::filesystem;
namespace pancakemusicbox {

// 싱글톤 인스턴스 생성
AudioScanner& AudioScanner::getInstance() {
    static AudioScanner instance;
    return instance;
}

// 생성자
AudioScanner::AudioScanner() {
    // 초기화 코드
    LOGI("AudioScanner initialized");
}

// 소멸자
AudioScanner::~AudioScanner() {
    // 정리 코드
    LOGI("AudioScanner destroyed");
}

// 디렉토리 스캔
bool AudioScanner::scanDirectory(const std::string& directoryPath, 
                             std::function<void(int, int)> progressCallback) {
    // 디렉토리 존재 확인
    try {
        if (!fs::exists(directoryPath)) {
            LOGE("Directory does not exist: %s", directoryPath.c_str());
            return false;
        }
    } catch (const std::exception& e) {
        LOGE("Error checking directory existence: %s, error: %s", directoryPath.c_str(), e.what());
        return false;
    }
    
    std::vector<std::string> audioFiles;
    
    // 지원하는 오디오 파일 확장자
    std::vector<std::string> supportedExtensions = {
        ".flac", ".wav", ".mp3", ".aac", ".ogg", ".m4a", ".dsf", ".dff", ".mqa"
    };
    
    // 모든 파일 탐색
    try {
        // 권한 문제로 디렉토리에 접근할 수 없을 수 있으므로 더 강력한 오류 처리 사용
        // 파일 시스템 오류 시 기본 디렉토리 접근 방식 대신 직접 구현
        LOGI("Starting scan of directory: %s", directoryPath.c_str());
        
        for (const auto& entry : fs::recursive_directory_iterator(
            directoryPath, 
            fs::directory_options::skip_permission_denied)) { // 권한 거부된 디렉토리 스킵
            try {
                if (fs::is_regular_file(entry.path())) {
                    std::string extension = entry.path().extension().string();
                    std::transform(extension.begin(), extension.end(), extension.begin(), ::tolower);
                    
                    // 지원하는 오디오 파일만 추가
                    if (std::find(supportedExtensions.begin(), supportedExtensions.end(), extension) != supportedExtensions.end()) {
                        audioFiles.push_back(entry.path().string());
                    }
                }
            } catch (const std::exception& e) {
                // 개별 파일 처리 오류 로깅 후 계속 진행
                LOGE("Error processing file entry: %s", e.what());
                continue;
            }
        }
    } catch (const fs::filesystem_error& e) {
        LOGE("Filesystem error scanning directory: %s", e.what());
        return false;
    } catch (const std::exception& e) {
        LOGE("Error scanning directory: %s", e.what());
        return false;
    }
    
    int totalFiles = audioFiles.size();
    int processedFiles = 0;
    
    if (totalFiles == 0) {
        LOGI("No audio files found in directory: %s", directoryPath.c_str());
        return true; // 파일이 없는 것은 오류가 아님
    }
    
    // 각 파일 스캔
    for (const auto& filePath : audioFiles) {
        try {
            scanFile(filePath);
        } catch (const std::exception& e) {
            // 개별 파일 스캔 실패 시 로깅하고 계속 진행
            LOGE("Error scanning file %s: %s", filePath.c_str(), e.what());
        }
        
        processedFiles++;
        if (progressCallback) {
            progressCallback(processedFiles, totalFiles);
        }
    }
    
    LOGI("Scanned %d audio files in directory: %s", processedFiles, directoryPath.c_str());
    return true;
}

// 파일 스캔 및 메타데이터 추출
bool AudioScanner::scanFile(const std::string& filePath) {
    try {
        // 파일 존재 확인
        bool fileExists = false;
        
        try {
            fileExists = fs::exists(filePath);
        } catch (const std::exception& e) {
            LOGE("Error checking if file exists: %s, error: %s", filePath.c_str(), e.what());
            return false;
        }
        
        if (!fileExists) {
            LOGE("File does not exist: %s", filePath.c_str());
            return false;
        }
        
        // 파일 접근이 가능한지 확인
        bool isAccessible = false;
        try {
            // 파일 크기를 얻을 수 있다면 파일이 접근 가능하다고 간주
            fs::file_size(filePath);
            isAccessible = true;
        } catch (const std::exception& e) {
            LOGE("File is not accessible: %s, error: %s", filePath.c_str(), e.what());
            return false;
        }
        
        if (!isAccessible) {
            LOGE("File exists but is not accessible: %s", filePath.c_str());
            return false;
        }
        
        TrackMetadata metadata;
        if (!extractMetadata(filePath, metadata)) {
            LOGE("Failed to extract metadata from file: %s", filePath.c_str());
            return false;
        }
        
        // 트랙 정보 저장
        std::lock_guard<std::mutex> lock(mutex);
        tracks[metadata.id] = metadata;
        
        // 앨범 정보 업데이트 또는 추가
        std::string albumKey = metadata.albumTitle + "_" + metadata.artist;
        if (albums.find(albumKey) == albums.end()) {
            AlbumMetadata album;
            album.id = generateId();
            album.title = metadata.albumTitle;
            album.artist = metadata.artist;
            album.artworkPath = metadata.albumArtPath;
            album.year = metadata.year;
            album.genre = metadata.genre;
            album.trackIds.push_back(metadata.id);
            albums[album.id] = album;
        } else {
            albums[albumKey].trackIds.push_back(metadata.id);
        }
        
        LOGI("Scanned file: %s", filePath.c_str());
        return true;
    } catch (const std::exception& e) {
        LOGE("Unexpected error scanning file %s: %s", filePath.c_str(), e.what());
        return false;
    }
}

// 파일에서 메타데이터 추출
bool AudioScanner::extractMetadata(const std::string& filePath, TrackMetadata& metadata) {
    // 이 함수는 실제로는 TagLib 같은 라이브러리를 사용하여 메타데이터를 추출해야 함
    // 여기서는 간단한 구현으로 파일 정보만 기반으로 기본 메타데이터 생성
    
    try {
        fs::path path(filePath);
        
        // 기본 정보 설정
        metadata.id = generateId();
        metadata.filePath = filePath;
        
        // 파일명에서 기본 정보 추출 시 예외 처리 강화
        try {
            metadata.title = path.stem().string();
        } catch (...) {
            metadata.title = "Unknown Title";
        }
        
        metadata.artist = "Unknown Artist";
        metadata.albumTitle = "Unknown Album";
        metadata.genre = "Unknown";
        metadata.year = 0;
        metadata.trackNumber = 0;
        metadata.playCount = 0;
        metadata.lastPlayed = 0;
        
        // 파일 크기 가져오기 시 예외 처리
        uintmax_t fileSize = 0;
        try {
            fileSize = fs::file_size(filePath);
        } catch (const std::exception& e) {
            LOGE("Error getting file size for %s: %s", filePath.c_str(), e.what());
            // 기본값으로 대체
            fileSize = 1024 * 300; // 약 3MB로 가정
        }
        
        // 파일 크기로 가상의 재생 시간 추정 (실제로는 TagLib 등에서 얻어야 함)
        metadata.duration = (fileSize / 1024) * 10; // 크기를 기반으로 한 임시 계산
        
        // 최소 재생 시간 설정 (방어 코드)
        if (metadata.duration < 1000) {
            metadata.duration = 1000; // 최소 1초
        }
        
        // 오디오 품질 정보 (확장자로 유추)
        std::string extension;
        try {
            extension = path.extension().string();
            std::transform(extension.begin(), extension.end(), extension.begin(), ::tolower);
        } catch (...) {
            extension = ".mp3"; // 기본값
        }
        
        metadata.audioQuality.format = getAudioFormatFromExtension(filePath);
        
        // 샘플링 레이트와 비트 뎁스는 실제로는 파일에서 추출해야 함
        if (metadata.audioQuality.format == "FLAC" || metadata.audioQuality.format == "WAV") {
            metadata.audioQuality.sampleRate = 44100;
            metadata.audioQuality.bitDepth = 16;
        } else if (metadata.audioQuality.format == "DSD") {
            metadata.audioQuality.sampleRate = 2822400;
            metadata.audioQuality.bitDepth = 1; // DSD는 1-bit
        } else if (metadata.audioQuality.format == "MQA") {
            metadata.audioQuality.sampleRate = 96000;
            metadata.audioQuality.bitDepth = 24;
        } else {
            metadata.audioQuality.sampleRate = 44100;
            metadata.audioQuality.bitDepth = 16;
        }
        
        metadata.audioQuality.channels = 2; // 스테레오 가정
        
        return true;
    } catch (const std::exception& e) {
        LOGE("Error extracting metadata: %s", e.what());
        return false;
    } catch (...) {
        LOGE("Unknown error extracting metadata for file: %s", filePath.c_str());
        return false;
    }
}

// 파일 확장자로 오디오 포맷 유추
std::string AudioScanner::getAudioFormatFromExtension(const std::string& filePath) {
    fs::path path(filePath);
    std::string extension = path.extension().string();
    std::transform(extension.begin(), extension.end(), extension.begin(), ::tolower);
    
    if (extension == ".flac") return "FLAC";
    else if (extension == ".wav") return "WAV";
    else if (extension == ".mp3") return "MP3";
    else if (extension == ".aac") return "AAC";
    else if (extension == ".ogg") return "OGG";
    else if (extension == ".m4a") return "AAC";
    else if (extension == ".dsf" || extension == ".dff") return "DSD";
    else if (extension == ".mqa") return "MQA";
    else return "Unknown";
}

// ID 생성
std::string AudioScanner::generateId() {
    static std::random_device rd;
    static std::mt19937 gen(rd());
    static std::uniform_int_distribution<> dis(0, 15);
    static const char* hex_chars = "0123456789abcdef";
    
    std::stringstream ss;
    for (int i = 0; i < 32; i++) {
        ss << hex_chars[dis(gen)];
        if (i == 7 || i == 11 || i == 15 || i == 19) {
            ss << '-';
        }
    }
    return ss.str();
}

// ID로 트랙 가져오기
TrackMetadata AudioScanner::getTrackById(const std::string& trackId) {
    std::lock_guard<std::mutex> lock(mutex);
    if (tracks.find(trackId) != tracks.end()) {
        return tracks[trackId];
    }
    return TrackMetadata();
}

// 모든 트랙 가져오기
std::vector<TrackMetadata> AudioScanner::getAllTracks() {
    std::lock_guard<std::mutex> lock(mutex);
    std::vector<TrackMetadata> result;
    for (const auto& pair : tracks) {
        result.push_back(pair.second);
    }
    return result;
}

// 장르별 트랙 가져오기
std::map<std::string, std::vector<TrackMetadata>> AudioScanner::getTracksByGenre() {
    std::lock_guard<std::mutex> lock(mutex);
    std::map<std::string, std::vector<TrackMetadata>> result;
    
    for (const auto& pair : tracks) {
        const TrackMetadata& track = pair.second;
        std::string genre = track.genre.empty() ? "Unknown" : track.genre;
        result[genre].push_back(track);
    }
    
    return result;
}

// 최근 재생 트랙 가져오기
std::vector<TrackMetadata> AudioScanner::getRecentlyPlayedTracks(int limit) {
    std::lock_guard<std::mutex> lock(mutex);
    std::vector<TrackMetadata> result;
    
    for (const auto& pair : tracks) {
        if (pair.second.lastPlayed > 0) {
            result.push_back(pair.second);
        }
    }
    
    // 최근 재생 순으로 정렬
    std::sort(result.begin(), result.end(), [](const TrackMetadata& a, const TrackMetadata& b) {
        return a.lastPlayed > b.lastPlayed;
    });
    
    // 제한된 수의 트랙만 반환
    if (result.size() > limit) {
        result.resize(limit);
    }
    
    return result;
}

// 자주 재생된 트랙 가져오기
std::vector<TrackMetadata> AudioScanner::getFrequentlyPlayedTracks(int limit) {
    std::lock_guard<std::mutex> lock(mutex);
    std::vector<TrackMetadata> result;
    
    for (const auto& pair : tracks) {
        if (pair.second.playCount > 0) {
            result.push_back(pair.second);
        }
    }
    
    // 재생 횟수로 정렬
    std::sort(result.begin(), result.end(), [](const TrackMetadata& a, const TrackMetadata& b) {
        return a.playCount > b.playCount;
    });
    
    // 제한된 수의 트랙만 반환
    if (result.size() > limit) {
        result.resize(limit);
    }
    
    return result;
}

// 최근 추가된 트랙 가져오기
std::vector<TrackMetadata> AudioScanner::getRecentlyAddedTracks(int limit) {
    std::lock_guard<std::mutex> lock(mutex);
    std::vector<TrackMetadata> result;
    
    // 파일 생성 시간을 기준으로 정렬
    std::vector<std::pair<TrackMetadata, time_t>> tracksWithTime;
    
    for (const auto& pair : tracks) {
        try {
            fs::path path(pair.second.filePath);
            auto fileTime = fs::last_write_time(path);
            // C++17에서는 file_clock이 없으므로 직접 time_t로 변환
            auto time_c = std::chrono::system_clock::to_time_t(std::chrono::system_clock::now());
            
            // 현재 시간을 사용하되, 파일 경로가 있으면 해당 파일의 마지막 수정 시간을 사용
            struct stat st;
            auto time_t_value = time_c;
            if (stat(pair.second.filePath.c_str(), &st) == 0) {
                time_t_value = st.st_mtime;
            }
            
            tracksWithTime.push_back({pair.second, time_t_value});
        } catch (const std::exception& e) {
            LOGE("Error getting file time: %s", e.what());
        }
    }
    
    // 최근 추가된 순으로 정렬
    std::sort(tracksWithTime.begin(), tracksWithTime.end(), 
             [](const auto& a, const auto& b) {
                 return a.second > b.second;
             });
    
    // 트랙 추출
    for (const auto& pair : tracksWithTime) {
        result.push_back(pair.first);
        if (result.size() >= limit) {
            break;
        }
    }
    
    return result;
}

// 재생 횟수 업데이트
void AudioScanner::updatePlayCount(const std::string& trackId) {
    std::lock_guard<std::mutex> lock(mutex);
    if (tracks.find(trackId) != tracks.end()) {
        tracks[trackId].playCount++;
    }
}

// 마지막 재생 시간 업데이트
void AudioScanner::updateLastPlayed(const std::string& trackId) {
    std::lock_guard<std::mutex> lock(mutex);
    if (tracks.find(trackId) != tracks.end()) {
        auto now = std::chrono::system_clock::now();
        auto timestamp = std::chrono::duration_cast<std::chrono::milliseconds>(
            now.time_since_epoch()).count();
        tracks[trackId].lastPlayed = timestamp;
    }
}

// 데이터베이스 저장
bool AudioScanner::saveDatabase(const std::string& dbFilePath) {
    // 실제 구현에서는 SQLite나 JSON 등으로 구현할 수 있음
    // 여기서는 간단한 예시로 바이너리 파일에 저장
    try {
        std::ofstream outFile(dbFilePath, std::ios::binary);
        if (!outFile) {
            LOGE("Failed to open database file for writing: %s", dbFilePath.c_str());
            return false;
        }
        
        // 트랙 수 저장
        size_t trackCount = tracks.size();
        outFile.write(reinterpret_cast<const char*>(&trackCount), sizeof(trackCount));
        
        // 각 트랙 저장
        for (const auto& pair : tracks) {
            const auto& track = pair.second;
            
            // 문자열 길이 및 데이터 저장 유틸리티 함수
            auto writeString = [&outFile](const std::string& str) {
                size_t len = str.length();
                outFile.write(reinterpret_cast<const char*>(&len), sizeof(len));
                if (len > 0) {
                    outFile.write(str.c_str(), len);
                }
            };
            
            // 트랙 데이터 저장
            writeString(track.id);
            writeString(track.title);
            writeString(track.artist);
            writeString(track.albumTitle);
            writeString(track.albumArtPath);
            outFile.write(reinterpret_cast<const char*>(&track.duration), sizeof(track.duration));
            writeString(track.filePath);
            
            // 오디오 품질 저장
            outFile.write(reinterpret_cast<const char*>(&track.audioQuality.bitDepth), sizeof(track.audioQuality.bitDepth));
            outFile.write(reinterpret_cast<const char*>(&track.audioQuality.sampleRate), sizeof(track.audioQuality.sampleRate));
            writeString(track.audioQuality.format);
            outFile.write(reinterpret_cast<const char*>(&track.audioQuality.channels), sizeof(track.audioQuality.channels));
            
            // 나머지 데이터
            writeString(track.genre);
            outFile.write(reinterpret_cast<const char*>(&track.year), sizeof(track.year));
            outFile.write(reinterpret_cast<const char*>(&track.trackNumber), sizeof(track.trackNumber));
            writeString(track.composer);
            outFile.write(reinterpret_cast<const char*>(&track.playCount), sizeof(track.playCount));
            outFile.write(reinterpret_cast<const char*>(&track.lastPlayed), sizeof(track.lastPlayed));
        }
        
        // 앨범 수 저장
        size_t albumCount = albums.size();
        outFile.write(reinterpret_cast<const char*>(&albumCount), sizeof(albumCount));
        
        // 각 앨범 저장
        for (const auto& pair : albums) {
            const auto& album = pair.second;
            
            auto writeString = [&outFile](const std::string& str) {
                size_t len = str.length();
                outFile.write(reinterpret_cast<const char*>(&len), sizeof(len));
                if (len > 0) {
                    outFile.write(str.c_str(), len);
                }
            };
            
            // 앨범 데이터 저장
            writeString(album.id);
            writeString(album.title);
            writeString(album.artist);
            writeString(album.artworkPath);
            outFile.write(reinterpret_cast<const char*>(&album.year), sizeof(album.year));
            writeString(album.genre);
            
            // 트랙 ID 목록 저장
            size_t trackIdCount = album.trackIds.size();
            outFile.write(reinterpret_cast<const char*>(&trackIdCount), sizeof(trackIdCount));
            
            for (const auto& trackId : album.trackIds) {
                writeString(trackId);
            }
        }
        
        outFile.close();
        LOGI("Database saved to %s", dbFilePath.c_str());
        return true;
    } catch (const std::exception& e) {
        LOGE("Error saving database: %s", e.what());
        return false;
    }
}

// 데이터베이스 로드
bool AudioScanner::loadDatabase(const std::string& dbFilePath) {
    try {
        std::ifstream inFile(dbFilePath, std::ios::binary);
        if (!inFile) {
            LOGE("Failed to open database file for reading: %s", dbFilePath.c_str());
            return false;
        }
        
        // 트랙 및 앨범 데이터 초기화
        std::lock_guard<std::mutex> lock(mutex);
        tracks.clear();
        albums.clear();
        
        // 문자열 읽기 유틸리티 함수
        auto readString = [&inFile]() -> std::string {
            size_t len;
            inFile.read(reinterpret_cast<char*>(&len), sizeof(len));
            
            if (len > 0) {
                std::vector<char> buffer(len);
                inFile.read(buffer.data(), len);
                return std::string(buffer.data(), len);
            }
            
            return "";
        };
        
        // 트랙 수 읽기
        size_t trackCount;
        inFile.read(reinterpret_cast<char*>(&trackCount), sizeof(trackCount));
        
        // 각 트랙 읽기
        for (size_t i = 0; i < trackCount; i++) {
            TrackMetadata track;
            
            track.id = readString();
            track.title = readString();
            track.artist = readString();
            track.albumTitle = readString();
            track.albumArtPath = readString();
            inFile.read(reinterpret_cast<char*>(&track.duration), sizeof(track.duration));
            track.filePath = readString();
            
            // 오디오 품질 읽기
            inFile.read(reinterpret_cast<char*>(&track.audioQuality.bitDepth), sizeof(track.audioQuality.bitDepth));
            inFile.read(reinterpret_cast<char*>(&track.audioQuality.sampleRate), sizeof(track.audioQuality.sampleRate));
            track.audioQuality.format = readString();
            inFile.read(reinterpret_cast<char*>(&track.audioQuality.channels), sizeof(track.audioQuality.channels));
            
            // 나머지 데이터 읽기
            track.genre = readString();
            inFile.read(reinterpret_cast<char*>(&track.year), sizeof(track.year));
            inFile.read(reinterpret_cast<char*>(&track.trackNumber), sizeof(track.trackNumber));
            track.composer = readString();
            inFile.read(reinterpret_cast<char*>(&track.playCount), sizeof(track.playCount));
            inFile.read(reinterpret_cast<char*>(&track.lastPlayed), sizeof(track.lastPlayed));
            
            // 트랙 맵에 추가
            tracks[track.id] = track;
        }
        
        // 앨범 수 읽기
        size_t albumCount;
        inFile.read(reinterpret_cast<char*>(&albumCount), sizeof(albumCount));
        
        // 각 앨범 읽기
        for (size_t i = 0; i < albumCount; i++) {
            AlbumMetadata album;
            
            album.id = readString();
            album.title = readString();
            album.artist = readString();
            album.artworkPath = readString();
            inFile.read(reinterpret_cast<char*>(&album.year), sizeof(album.year));
            album.genre = readString();
            
            // 트랙 ID 목록 읽기
            size_t trackIdCount;
            inFile.read(reinterpret_cast<char*>(&trackIdCount), sizeof(trackIdCount));
            
            for (size_t j = 0; j < trackIdCount; j++) {
                std::string trackId = readString();
                album.trackIds.push_back(trackId);
            }
            
            // 앨범 맵에 추가
            albums[album.id] = album;
        }
        
        inFile.close();
        LOGI("Database loaded from %s, %zu tracks, %zu albums", 
            dbFilePath.c_str(), tracks.size(), albums.size());
        return true;
    } catch (const std::exception& e) {
        LOGE("Error loading database: %s", e.what());
        return false;
    }
}

} // namespace pancakemusicbox
