
#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "include/AudioPlayer.h"
#include "include/AudioScanner.h"
#include "include/JNIBridge.h"

#define LOG_TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace pancakemusicbox;

// 오디오 플레이어 싱글톤 인스턴스 접근 헬퍼
static AudioPlayer& getPlayer() {
    return AudioPlayer::getInstance();
}

// JavaVM 글로벌 참조
static JavaVM* javaVM = nullptr;

// JNI_OnLoad 함수 - 라이브러리가 로드될 때 호출됨
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    javaVM = vm;
    JNIBridge::setJavaVM(vm);
    
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    
    LOGI("JNI_OnLoad called, native library loaded");
    
    return JNI_VERSION_1_6;
}

// 오디오 플레이어 관련 JNI 함수들

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_pancakemusicbox_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from HIFI Player Native Engine";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeLoadFile(
        JNIEnv* env,
        jobject /* this */,
        jstring jFilePath) {
    return static_cast<jboolean>(getPlayer().loadFile(env, jFilePath));
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativePlay(
        JNIEnv* env,
        jobject /* this */) {
    getPlayer().play();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativePause(
        JNIEnv* env,
        jobject /* this */) {
    getPlayer().pause();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeStop(
        JNIEnv* env,
        jobject /* this */) {
    getPlayer().stop();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSeekTo(
        JNIEnv* env,
        jobject /* this */,
        jlong positionMs) {
    getPlayer().seekTo(positionMs);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeIsPlaying(
        JNIEnv* env,
        jobject /* this */) {
    return static_cast<jboolean>(getPlayer().isPlaying());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeGetCurrentPosition(
        JNIEnv* env,
        jobject /* this */) {
    return static_cast<jlong>(getPlayer().getCurrentPosition());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeGetDuration(
        JNIEnv* env,
        jobject /* this */) {
    return static_cast<jlong>(getPlayer().getDuration());
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetSampleRate(
        JNIEnv* env,
        jobject /* this */,
        jint sampleRate) {
    getPlayer().setSampleRate(sampleRate);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetBitDepth(
        JNIEnv* env,
        jobject /* this */,
        jint bitDepth) {
    getPlayer().setBitDepth(bitDepth);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetChannelCount(
        JNIEnv* env,
        jobject /* this */,
        jint channelCount) {
    getPlayer().setChannelCount(channelCount);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetVolume(
        JNIEnv* env,
        jobject /* this */,
        jfloat volume) {
    getPlayer().setVolume(volume);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeEnableEQ(
        JNIEnv* env,
        jobject /* this */,
        jboolean enable) {
    getPlayer().enableEQ(enable);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetEQBand(
        JNIEnv* env,
        jobject /* this */,
        jint band,
        jfloat gain) {
    getPlayer().setEQBand(band, gain);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeEnableVolumeNormalization(
        JNIEnv* env,
        jobject /* this */,
        jboolean enable) {
    getPlayer().enableVolumeNormalization(enable);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeSetTargetLUFS(
        JNIEnv* env,
        jobject /* this */,
        jfloat lufsValue) {
    getPlayer().setTargetLUFS(lufsValue);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeOptimizeForDevice(
        JNIEnv* env,
        jobject /* this */,
        jboolean useHeadphones,
        jboolean isHighPerformanceDevice) {
    getPlayer().optimizeForDevice(useHeadphones, isHighPerformanceDevice);
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_pancakemusicbox_audio_AudioPlayerNative_nativeGetVisualizationData(
        JNIEnv* env,
        jobject /* this */) {
    std::vector<float> vizData = getPlayer().getVisualizationData();
    
    jfloatArray result = env->NewFloatArray(vizData.size());
    if (result == nullptr) {
        return nullptr; // OutOfMemoryError
    }
    
    env->SetFloatArrayRegion(result, 0, vizData.size(), vizData.data());
    return result;
}

// AudioScanner 관련 JNI 함수 구현

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_scanDirectory(
        JNIEnv* env,
        jobject /* this */,
        jstring jDirectoryPath) {
    
    std::string directoryPath = JNIBridge::toString(env, jDirectoryPath);
    
    // 프로그레스 콜백을 위한 전역 참조 얻기
    jclass scannerClass = env->FindClass("com/example/pancakemusicbox/audio/AudioScannerNative");
    jmethodID onProgressMethod = env->GetStaticMethodID(scannerClass, "onScanProgress", "(II)V");
    
    bool success = AudioScanner::getInstance().scanDirectory(directoryPath, 
                                                           [env, scannerClass, onProgressMethod](int current, int total) {
        env->CallStaticVoidMethod(scannerClass, onProgressMethod, current, total);
    });
    
    env->DeleteLocalRef(scannerClass);
    
    return static_cast<jboolean>(success);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_scanFile(
        JNIEnv* env,
        jobject /* this */,
        jstring jFilePath) {
    
    std::string filePath = JNIBridge::toString(env, jFilePath);
    bool success = AudioScanner::getInstance().scanFile(filePath);
    
    return static_cast<jboolean>(success);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getTrackById(
        JNIEnv* env,
        jobject /* this */,
        jstring jTrackId) {
    
    std::string trackId = JNIBridge::toString(env, jTrackId);
    TrackMetadata metadata = AudioScanner::getInstance().getTrackById(trackId);
    
    // ID가 비어있으면 트랙을 찾지 못한 것
    if (metadata.id.empty()) {
        return nullptr;
    }
    
    return JNIBridge::toJavaTrack(env, metadata);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getAllTracks(
        JNIEnv* env,
        jobject /* this */) {
    
    std::vector<TrackMetadata> tracks = AudioScanner::getInstance().getAllTracks();
    return JNIBridge::toJavaTrackList(env, tracks);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getTracksByGenre(
        JNIEnv* env,
        jobject /* this */) {
    
    std::map<std::string, std::vector<TrackMetadata>> genreTracks = 
        AudioScanner::getInstance().getTracksByGenre();
    
    return JNIBridge::toJavaGenreTrackMap(env, genreTracks);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getRecentlyPlayedTracks(
        JNIEnv* env,
        jobject /* this */,
        jint limit) {
    
    std::vector<TrackMetadata> tracks = 
        AudioScanner::getInstance().getRecentlyPlayedTracks(limit);
    
    return JNIBridge::toJavaTrackList(env, tracks);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getFrequentlyPlayedTracks(
        JNIEnv* env,
        jobject /* this */,
        jint limit) {
    
    std::vector<TrackMetadata> tracks = 
        AudioScanner::getInstance().getFrequentlyPlayedTracks(limit);
    
    return JNIBridge::toJavaTrackList(env, tracks);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_getRecentlyAddedTracks(
        JNIEnv* env,
        jobject /* this */,
        jint limit) {
    
    std::vector<TrackMetadata> tracks = 
        AudioScanner::getInstance().getRecentlyAddedTracks(limit);
    
    return JNIBridge::toJavaTrackList(env, tracks);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_updatePlayCount(
        JNIEnv* env,
        jobject /* this */,
        jstring jTrackId) {
    
    std::string trackId = JNIBridge::toString(env, jTrackId);
    AudioScanner::getInstance().updatePlayCount(trackId);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_updateLastPlayed(
        JNIEnv* env,
        jobject /* this */,
        jstring jTrackId) {
    
    std::string trackId = JNIBridge::toString(env, jTrackId);
    AudioScanner::getInstance().updateLastPlayed(trackId);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_saveDatabase(
        JNIEnv* env,
        jobject /* this */,
        jstring jDbFilePath) {
    
    std::string dbFilePath = JNIBridge::toString(env, jDbFilePath);
    bool success = AudioScanner::getInstance().saveDatabase(dbFilePath);
    
    return static_cast<jboolean>(success);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_loadDatabase(
        JNIEnv* env,
        jobject /* this */,
        jstring jDbFilePath) {
    
    std::string dbFilePath = JNIBridge::toString(env, jDbFilePath);
    bool success = AudioScanner::getInstance().loadDatabase(dbFilePath);
    
    return static_cast<jboolean>(success);
}
