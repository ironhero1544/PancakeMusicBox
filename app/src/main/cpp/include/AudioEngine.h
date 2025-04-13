#pragma once

#include <oboe/Oboe.h>
#include <vector>
#include <string>
#include <mutex>
#include <memory>

/**
 * HiFi 오디오 플레이어를 위한 오디오 엔진 클래스
 * Oboe 라이브러리를 사용하여 고품질 오디오 재생 구현
 */
class AudioEngine : public oboe::AudioStreamCallback {
public:
    AudioEngine();
    ~AudioEngine();

    // 오디오 파일 로드 및 재생 관련 함수
    bool loadFile(const std::string& filePath);
    void play();
    void pause();
    void stop();
    void seekTo(int64_t positionMs);
    bool isPlaying() const;
    int64_t getCurrentPosition() const;
    int64_t getDuration() const;

    // 오디오 품질 및 설정 관련 함수
    void setSampleRate(int sampleRate);
    void setBitDepth(int bitDepth);
    void setChannelCount(int channelCount);
    void setVolume(float volume);
    
    // 하드웨어별 최적화 설정
    void optimizeForDevice(bool useHeadphones, bool isHighPerformanceDevice);

    // 오디오 처리 관련 함수 (EQ, 볼륨 정규화 등)
    void enableEQ(bool enable);
    void setEQBand(int band, float gain);
    void enableVolumeNormalization(bool enable);
    void setTargetLUFS(float lufsValue);

    // 오디오 스트림 콜백 함수 (Oboe 요구사항)
    oboe::DataCallbackResult onAudioReady(
        oboe::AudioStream *oboeStream,
        void *audioData,
        int32_t numFrames) override;

    // 오디오 스트림 에러 콜백 함수 (Oboe 요구사항)
    void onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error) override;
    void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) override;

private:
    // 오디오 스트림 생성 및 관리
    bool openOutputStream();
    void closeOutputStream();
    bool restartStream();
    
    // 오디오 포맷 변환 및 처리
    void processAudioData(float* audioData, int32_t numFrames);
    void applyEQ(float* audioData, int32_t numFrames);
    void applyVolumeNormalization(float* audioData, int32_t numFrames);
    
    // 오디오 분석 및 시각화 데이터 생성
    void updateVisualizationData(const float* audioData, int32_t numFrames);

    // Oboe 스트림 객체
    std::shared_ptr<oboe::AudioStream> mAudioStream;
    
    // 오디오 데이터 버퍼 및 상태 관리
    std::vector<float> mAudioData;
    std::vector<float> mVisualizationData;
    int64_t mCurrentFrame = 0;
    int64_t mTotalFrames = 0;
    bool mIsPlaying = false;
    
    // 오디오 포맷 및 설정
    int mSampleRate = 44100;
    int mChannelCount = 2;
    int mBitDepth = 16;
    float mVolume = 1.0f;
    
    // 오디오 처리 설정
    bool mEQEnabled = false;
    std::vector<float> mEQGains;
    bool mVolumeNormalizationEnabled = false;
    float mTargetLUFS = -14.0f; // 기본 타겟 LUFS 값
    
    // 스레드 안전성을 위한 뮤텍스
    mutable std::mutex mLock;
};
