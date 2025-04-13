#include "include/AudioEngine.h"
#include <android/log.h>
#include <cmath>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

#define LOG_TAG "AudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

AudioEngine::AudioEngine() {
    // EQ 밴드 초기화 (10 밴드 EQ)
    mEQGains.resize(10, 0.0f);
    
    // 시각화 데이터 버퍼 초기화
    mVisualizationData.resize(20, 0.0f);
    
    LOGI("AudioEngine created");
}

AudioEngine::~AudioEngine() {
    closeOutputStream();
    LOGI("AudioEngine destroyed");
}

bool AudioEngine::loadFile(const std::string& filePath) {
    std::lock_guard<std::mutex> lock(mLock);
    
    // 현재 재생 중인 스트림 정리
    stop();
    
    LOGI("Loading file: %s", filePath.c_str());
    
    // 실제 구현에서는 여기서 오디오 파일을 로드하고 디코딩해야 함
    // 이 예제에서는 로드 성공을 가정하고 간단한 사인파 형태의 데이터 생성
    
    mSampleRate = 44100;
    mChannelCount = 2;
    mBitDepth = 16;
    
    // 임의의 오디오 데이터 생성 (20초 길이의 사인파)
    int totalFrames = mSampleRate * 20; // 20초
    mAudioData.resize(totalFrames * mChannelCount);
    
    // 440Hz 사인파 생성
    float frequency = 440.0f;
    for (int i = 0; i < totalFrames; i++) {
        float sample = 0.5f * sinf(2.0f * M_PI * frequency * i / mSampleRate);
        for (int ch = 0; ch < mChannelCount; ch++) {
            mAudioData[i * mChannelCount + ch] = sample;
        }
    }
    
    mTotalFrames = totalFrames;
    mCurrentFrame = 0;
    
    // 오디오 스트림 설정
    bool result = openOutputStream();
    if (result) {
        LOGI("File loaded successfully");
    } else {
        LOGE("Failed to load file or open output stream");
    }
    
    return result;
}

void AudioEngine::play() {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (!mAudioStream || mAudioData.empty()) {
        LOGE("Cannot play: stream not open or no audio data");
        return;
    }
    
    if (mAudioStream->getState() != oboe::StreamState::Started) {
        oboe::Result result = mAudioStream->requestStart();
        if (result != oboe::Result::OK) {
            LOGE("Error starting stream: %s", oboe::convertToText(result));
            return;
        }
    }
    
    mIsPlaying = true;
    LOGI("Audio playback started");
}

void AudioEngine::pause() {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (mIsPlaying && mAudioStream) {
        oboe::Result result = mAudioStream->requestPause();
        if (result != oboe::Result::OK) {
            LOGE("Error pausing stream: %s", oboe::convertToText(result));
            return;
        }
        
        mIsPlaying = false;
        LOGI("Audio playback paused");
    }
}

void AudioEngine::stop() {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (mAudioStream) {
        oboe::Result result = mAudioStream->requestStop();
        if (result != oboe::Result::OK) {
            LOGE("Error stopping stream: %s", oboe::convertToText(result));
        }
        
        mIsPlaying = false;
        mCurrentFrame = 0;
        LOGI("Audio playback stopped");
    }
}

void AudioEngine::seekTo(int64_t positionMs) {
    std::lock_guard<std::mutex> lock(mLock);
    
    int64_t newFrame = (positionMs * mSampleRate) / 1000;
    if (newFrame < 0) newFrame = 0;
    if (newFrame >= mTotalFrames) newFrame = mTotalFrames - 1;
    
    mCurrentFrame = newFrame;
    LOGI("Seek to position: %lld ms (frame %lld)", positionMs, newFrame);
}

bool AudioEngine::isPlaying() const {
    std::lock_guard<std::mutex> lock(mLock);
    return mIsPlaying;
}

int64_t AudioEngine::getCurrentPosition() const {
    std::lock_guard<std::mutex> lock(mLock);
    return (mCurrentFrame * 1000) / mSampleRate;
}

int64_t AudioEngine::getDuration() const {
    std::lock_guard<std::mutex> lock(mLock);
    return (mTotalFrames * 1000) / mSampleRate;
}

void AudioEngine::setSampleRate(int sampleRate) {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (mSampleRate != sampleRate) {
        mSampleRate = sampleRate;
        LOGI("Sample rate changed to %d", sampleRate);
        
        // 여기서 오디오 데이터 리샘플링 필요
        // 실제 구현에서는 리샘플링 라이브러리(SRC 등) 사용해야 함
        
        // 스트림 재시작
        if (mAudioStream) {
            restartStream();
        }
    }
}

void AudioEngine::setBitDepth(int bitDepth) {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (mBitDepth != bitDepth) {
        mBitDepth = bitDepth;
        LOGI("Bit depth changed to %d", bitDepth);
        
        // 스트림 포맷 업데이트 필요
        // 실제 구현에서는 비트 뎁스에 따라 데이터 포맷 변환
        
        // 스트림 재시작
        if (mAudioStream) {
            restartStream();
        }
    }
}

void AudioEngine::setChannelCount(int channelCount) {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (mChannelCount != channelCount && (channelCount == 1 || channelCount == 2)) {
        mChannelCount = channelCount;
        LOGI("Channel count changed to %d", channelCount);
        
        // 채널 수 변환 필요
        // 실제 구현에서는 믹싱 또는 채널 분리 작업 필요
        
        // 스트림 재시작
        if (mAudioStream) {
            restartStream();
        }
    }
}

void AudioEngine::setVolume(float volume) {
    std::lock_guard<std::mutex> lock(mLock);
    
    mVolume = volume;
    LOGI("Volume changed to %f", volume);
}

void AudioEngine::enableEQ(bool enable) {
    std::lock_guard<std::mutex> lock(mLock);
    
    mEQEnabled = enable;
    LOGI("EQ %s", enable ? "enabled" : "disabled");
}

void AudioEngine::setEQBand(int band, float gain) {
    std::lock_guard<std::mutex> lock(mLock);
    
    if (band >= 0 && band < mEQGains.size()) {
        mEQGains[band] = gain;
        LOGI("EQ band %d gain set to %f", band, gain);
    }
}

void AudioEngine::enableVolumeNormalization(bool enable) {
    std::lock_guard<std::mutex> lock(mLock);
    
    mVolumeNormalizationEnabled = enable;
    LOGI("Volume normalization %s", enable ? "enabled" : "disabled");
}

void AudioEngine::setTargetLUFS(float lufsValue) {
    std::lock_guard<std::mutex> lock(mLock);
    
    mTargetLUFS = lufsValue;
    LOGI("Target LUFS set to %f", lufsValue);
}

void AudioEngine::optimizeForDevice(bool useHeadphones, bool isHighPerformanceDevice) {
    std::lock_guard<std::mutex> lock(mLock);
    
    LOGI("Optimizing for device: useHeadphones=%d, highPerformance=%d", 
         useHeadphones, isHighPerformanceDevice);
    
    // 실제 구현에서는 기기 특성에 맞게 버퍼 크기, 지연 설정, 성능 설정 등 조정
    // 여기서는 구현 생략
}

bool AudioEngine::openOutputStream() {
    // 기존 스트림 종료
    closeOutputStream();
    
    // 출력 스트림 설정
    oboe::AudioStreamBuilder builder;
    
    // 스트림 설정
    builder.setDirection(oboe::Direction::Output)
           ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
           ->setSharingMode(oboe::SharingMode::Exclusive)
           ->setFormat(oboe::AudioFormat::Float)
           ->setChannelCount(mChannelCount)
           ->setSampleRate(mSampleRate)
           ->setCallback(this);
    
    // 스트림 생성
    oboe::Result result = builder.openStream(mAudioStream);
    if (result != oboe::Result::OK) {
        LOGE("Failed to open output stream: %s", oboe::convertToText(result));
        return false;
    }
    
    LOGI("Audio stream opened: %d channels, %d Hz", 
         mAudioStream->getChannelCount(),
         mAudioStream->getSampleRate());
    
    return true;
}

void AudioEngine::closeOutputStream() {
    if (mAudioStream) {
        mAudioStream->close();
        mAudioStream.reset();
        LOGI("Audio stream closed");
    }
}

bool AudioEngine::restartStream() {
    bool wasPlaying = mIsPlaying;
    
    closeOutputStream();
    bool result = openOutputStream();
    
    if (result && wasPlaying) {
        play();
    }
    
    return result;
}

oboe::DataCallbackResult AudioEngine::onAudioReady(
    oboe::AudioStream *oboeStream,
    void *audioData,
    int32_t numFrames) {
    
    // 오디오 데이터 버퍼
    float *outputBuffer = static_cast<float *>(audioData);
    
    // 오디오 처리 뮤텍스 락
    std::lock_guard<std::mutex> lock(mLock);
    
    // 재생 중이 아니면 무음 출력
    if (!mIsPlaying || mAudioData.empty()) {
        memset(outputBuffer, 0, sizeof(float) * numFrames * mChannelCount);
        return oboe::DataCallbackResult::Continue;
    }
    
    // 현재 프레임부터 버퍼 채우기
    int framesRemaining = mTotalFrames - mCurrentFrame;
    int framesToCopy = std::min<int>(numFrames, framesRemaining);
    
    if (framesToCopy > 0) {
        // 오디오 데이터 복사
        memcpy(outputBuffer, 
               mAudioData.data() + mCurrentFrame * mChannelCount, 
               framesToCopy * mChannelCount * sizeof(float));
        
        // 남은 프레임은 무음으로 채우기
        if (framesToCopy < numFrames) {
            memset(outputBuffer + framesToCopy * mChannelCount, 
                   0, 
                   (numFrames - framesToCopy) * mChannelCount * sizeof(float));
        }
        
        // 오디오 데이터 처리 (볼륨, EQ, 정규화 등)
        processAudioData(outputBuffer, framesToCopy);
        
        // 시각화 데이터 업데이트
        updateVisualizationData(outputBuffer, framesToCopy);
        
        // 현재 프레임 위치 업데이트
        mCurrentFrame += framesToCopy;
        
        // 재생 종료 체크
        if (mCurrentFrame >= mTotalFrames) {
            // 여기서 플레이백 완료 콜백을 트리거할 수 있음
            // 실제 구현에서는 재생 완료 이벤트를 Java 코드로 보내야 함
            LOGI("End of playback reached");
            mIsPlaying = false;
        }
    } else {
        // 재생할 프레임이 없으면 무음 출력
        memset(outputBuffer, 0, sizeof(float) * numFrames * mChannelCount);
        mIsPlaying = false;
    }
    
    return oboe::DataCallbackResult::Continue;
}

void AudioEngine::onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error) {
    LOGE("Audio stream error before close: %s", oboe::convertToText(error));
}

void AudioEngine::onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) {
    LOGE("Audio stream error after close: %s", oboe::convertToText(error));
    
    // 에러 발생 시 자동 스트림 재시작 시도
    std::lock_guard<std::mutex> lock(mLock);
    if (error != oboe::Result::OK) {
        restartStream();
    }
}

void AudioEngine::processAudioData(float* audioData, int32_t numFrames) {
    // 볼륨 적용
    if (mVolume != 1.0f) {
        for (int i = 0; i < numFrames * mChannelCount; i++) {
            audioData[i] *= mVolume;
        }
    }
    
    // EQ 적용
    if (mEQEnabled) {
        applyEQ(audioData, numFrames);
    }
    
    // 볼륨 정규화 적용
    if (mVolumeNormalizationEnabled) {
        applyVolumeNormalization(audioData, numFrames);
    }
}

void AudioEngine::applyEQ(float* audioData, int32_t numFrames) {
    // 여기서 실제 EQ 처리
    // 실제 구현에서는 FFT 기반 EQ 필터링이 필요함
    // 이 예제에서는 단순화를 위해 생략
}

void AudioEngine::applyVolumeNormalization(float* audioData, int32_t numFrames) {
    // 여기서 LUFS 기반 볼륨 정규화 처리
    // 실제 구현에서는 LUFS 측정 및 게인 조정이 필요함
    // 이 예제에서는 단순화를 위해 생략
}

void AudioEngine::updateVisualizationData(const float* audioData, int32_t numFrames) {
    // 간단한 RMS 기반 시각화 데이터 생성
    // 실제 구현에서는 FFT 기반 주파수 분석이 필요할 수 있음
    
    const int visualBands = mVisualizationData.size();
    const int framesPerBand = numFrames / visualBands;
    
    if (framesPerBand <= 0) return;
    
    for (int band = 0; band < visualBands; band++) {
        float sum = 0.0f;
        int startFrame = band * framesPerBand;
        int endFrame = startFrame + framesPerBand;
        
        for (int frame = startFrame; frame < endFrame && frame < numFrames; frame++) {
            for (int ch = 0; ch < mChannelCount; ch++) {
                float sample = audioData[frame * mChannelCount + ch];
                sum += sample * sample;
            }
        }
        
        // RMS 계산
        float rms = sqrtf(sum / (framesPerBand * mChannelCount));
        
        // 부드러운 애니메이션을 위한 간단한 보간
        float smoothingFactor = 0.3f;
        mVisualizationData[band] = mVisualizationData[band] * (1 - smoothingFactor) + rms * smoothingFactor;
    }
}
