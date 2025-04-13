#pragma once

#include <jni.h>
#include <string>
#include <memory>
#include "AudioEngine.h"

/**
 * JNI 인터페이스를 통한 AudioEngine 관리 클래스
 */
class AudioPlayer {
public:
    AudioPlayer();
    ~AudioPlayer();

    // 싱글톤 인스턴스 접근
    static AudioPlayer& getInstance();

    // JNI 함수
    bool loadFile(JNIEnv* env, jstring jFilePath);
    void play();
    void pause();
    void stop();
    void seekTo(int64_t positionMs);
    bool isPlaying() const;
    int64_t getCurrentPosition() const;
    int64_t getDuration() const;

    // 오디오 설정 함수
    void setSampleRate(int sampleRate);
    void setBitDepth(int bitDepth);
    void setChannelCount(int channelCount);
    void setVolume(float volume);

    // EQ 및 오디오 처리 함수
    void enableEQ(bool enable);
    void setEQBand(int band, float gain);
    void enableVolumeNormalization(bool enable);
    void setTargetLUFS(float lufsValue);

    // 하드웨어 최적화
    void optimizeForDevice(bool useHeadphones, bool isHighPerformanceDevice);

    // 시각화 데이터 얻기
    std::vector<float> getVisualizationData();

private:
    // 싱글톤 구현을 위한 숨겨진 생성자 및 복사 금지
    AudioPlayer(const AudioPlayer&) = delete;
    AudioPlayer& operator=(const AudioPlayer&) = delete;

    // 오디오 엔진 인스턴스
    std::unique_ptr<AudioEngine> mAudioEngine;
};
