#ifndef PANCAKEMUSICBOX_AUDIOSCANNER_H
#define PANCAKEMUSICBOX_AUDIOSCANNER_H

#include <string>
#include <vector>
#include <map>
#include <memory>
#include <functional>
#include <mutex>
#include "AudioMetadata.h"

namespace pancakemusicbox {

/**
 * 오디오 파일 스캐닝, 메타데이터 추출 및 관리를 위한 클래스
 */
class AudioScanner {
public:
    // 싱글톤 인스턴스 얻기
    static AudioScanner& getInstance();
    
    // 경로에 있는 모든 오디오 파일 스캔
    bool scanDirectory(const std::string& directoryPath, 
                     std::function<void(int, int)> progressCallback = nullptr);
    
    // 단일 오디오 파일 스캔 및 메타데이터 추출
    bool scanFile(const std::string& filePath);
    
    // ID로 트랙 정보 가져오기
    TrackMetadata getTrackById(const std::string& trackId);
    
    // 모든 트랙 정보 가져오기
    std::vector<TrackMetadata> getAllTracks();
    
    // 장르별 트랙 정보 가져오기
    std::map<std::string, std::vector<TrackMetadata>> getTracksByGenre();
    
    // 최근 재생 트랙 가져오기 (lastPlayed 기준)
    std::vector<TrackMetadata> getRecentlyPlayedTracks(int limit = 10);
    
    // 자주 재생된 트랙 가져오기 (playCount 기준)
    std::vector<TrackMetadata> getFrequentlyPlayedTracks(int limit = 10);
    
    // 최근 추가된 트랙 가져오기 (filePath의 생성 시간 기준)
    std::vector<TrackMetadata> getRecentlyAddedTracks(int limit = 10);
    
    // 트랙 재생 횟수 업데이트
    void updatePlayCount(const std::string& trackId);
    
    // 트랙 마지막 재생 시간 업데이트
    void updateLastPlayed(const std::string& trackId);
    
    // 데이터베이스 저장
    bool saveDatabase(const std::string& dbFilePath);
    
    // 데이터베이스 로드
    bool loadDatabase(const std::string& dbFilePath);
    
private:
    // 생성자, 소멸자 (싱글톤 패턴)
    AudioScanner();
    ~AudioScanner();
    
    // 복사 생성자 및 할당 연산자 삭제 (싱글톤 패턴)
    AudioScanner(const AudioScanner&) = delete;
    AudioScanner& operator=(const AudioScanner&) = delete;
    
    // 추가된 메소드 선언: generateId() (메소드 선언관련 버그 수정)
    std::string generateId();
    
    // 파일 확장자로 오디오 포맷 유추
    std::string getAudioFormatFromExtension(const std::string& filePath);
    
    // 파일에서 메타데이터 추출
    bool extractMetadata(const std::string& filePath, TrackMetadata& metadata);
    
    // 데이터 저장소
    std::map<std::string, TrackMetadata> tracks;
    std::map<std::string, AlbumMetadata> albums;
    std::map<std::string, PlaylistMetadata> playlists;
    
    // 스레드 안전을 위한 뮤텍스
    std::mutex mutex;
};

} // namespace pancakemusicbox

#endif // PANCAKEMUSICBOX_AUDIOSCANNER_H
