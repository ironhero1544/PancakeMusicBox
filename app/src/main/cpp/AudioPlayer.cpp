#include "include/AudioPlayer.h"
#include <android/log.h>

#define LOG_TAG "AudioPlayer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

AudioPlayer::AudioPlayer() : mAudioEngine(std::make_unique<AudioEngine>()) {
    LOGI("AudioPlayer created");
}

AudioPlayer::~AudioPlayer() {
    LOGI("AudioPlayer destroyed");
}

AudioPlayer& AudioPlayer::getInstance() {
    static AudioPlayer instance;
    return instance;
}

bool AudioPlayer::loadFile(JNIEnv* env, jstring jFilePath) {
    const char* filePath = env->GetStringUTFChars(jFilePath, nullptr);
    bool result = mAudioEngine->loadFile(filePath);
    env->ReleaseStringUTFChars(jFilePath, filePath);
    return result;
}

void AudioPlayer::play() {
    mAudioEngine->play();
}

void AudioPlayer::pause() {
    mAudioEngine->pause();
}

void AudioPlayer::stop() {
    mAudioEngine->stop();
}

void AudioPlayer::seekTo(int64_t positionMs) {
    mAudioEngine->seekTo(positionMs);
}

bool AudioPlayer::isPlaying() const {
    return mAudioEngine->isPlaying();
}

int64_t AudioPlayer::getCurrentPosition() const {
    return mAudioEngine->getCurrentPosition();
}

int64_t AudioPlayer::getDuration() const {
    return mAudioEngine->getDuration();
}

void AudioPlayer::setSampleRate(int sampleRate) {
    mAudioEngine->setSampleRate(sampleRate);
}

void AudioPlayer::setBitDepth(int bitDepth) {
    mAudioEngine->setBitDepth(bitDepth);
}

void AudioPlayer::setChannelCount(int channelCount) {
    mAudioEngine->setChannelCount(channelCount);
}

void AudioPlayer::setVolume(float volume) {
    mAudioEngine->setVolume(volume);
}

void AudioPlayer::enableEQ(bool enable) {
    mAudioEngine->enableEQ(enable);
}

void AudioPlayer::setEQBand(int band, float gain) {
    mAudioEngine->setEQBand(band, gain);
}

void AudioPlayer::enableVolumeNormalization(bool enable) {
    mAudioEngine->enableVolumeNormalization(enable);
}

void AudioPlayer::setTargetLUFS(float lufsValue) {
    mAudioEngine->setTargetLUFS(lufsValue);
}

void AudioPlayer::optimizeForDevice(bool useHeadphones, bool isHighPerformanceDevice) {
    mAudioEngine->optimizeForDevice(useHeadphones, isHighPerformanceDevice);
}

std::vector<float> AudioPlayer::getVisualizationData() {
    return std::vector<float>(20, 0.5f); // 샘플 시각화 데이터 반환
}
