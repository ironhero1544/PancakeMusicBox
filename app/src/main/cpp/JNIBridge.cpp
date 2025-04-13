#include "include/JNIBridge.h"
#include "include/AudioScanner.h"
#include <jni.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "JNIBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace pancakemusicbox;

// 정적 멤버 변수 초기화
JavaVM* JNIBridge::javaVM = nullptr;
jclass JNIBridge::trackClass = nullptr;
jclass JNIBridge::audioQualityClass = nullptr;
jclass JNIBridge::arrayListClass = nullptr;
jclass JNIBridge::hashMapClass = nullptr;
jmethodID JNIBridge::trackConstructor = nullptr;
jmethodID JNIBridge::audioQualityConstructor = nullptr;
jmethodID JNIBridge::arrayListConstructor = nullptr;
jmethodID JNIBridge::arrayListAdd = nullptr;
jmethodID JNIBridge::hashMapConstructor = nullptr;
jmethodID JNIBridge::hashMapPut = nullptr;

void JNIBridge::setJavaVM(JavaVM* vm) {
    javaVM = vm;
    
    // Java VM이 설정되면 필요한 클래스와 메서드 참조를 캐시
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Failed to get JNI environment in setJavaVM");
        return;
    }
    
    // 클래스 참조 가져오기 및 전역 참조로 변환
    jclass localTrackClass = env->FindClass("com/example/pancakemusicbox/model/Track");
    if (localTrackClass != nullptr) {
        trackClass = (jclass)env->NewGlobalRef(localTrackClass);
        env->DeleteLocalRef(localTrackClass);
        
        // Track 생성자 참조 가져오기
        trackConstructor = env->GetMethodID(trackClass, "<init>", 
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Lcom/example/pancakemusicbox/model/AudioQuality;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;ILjava/lang/Long;)V");
    } else {
        LOGE("Failed to find Track class");
    }
    
    jclass localAudioQualityClass = env->FindClass("com/example/pancakemusicbox/model/AudioQuality");
    if (localAudioQualityClass != nullptr) {
        audioQualityClass = (jclass)env->NewGlobalRef(localAudioQualityClass);
        env->DeleteLocalRef(localAudioQualityClass);
        
        // AudioQuality 생성자 참조 가져오기
        audioQualityConstructor = env->GetMethodID(audioQualityClass, "<init>", 
            "(IILjava/lang/String;ILjava/lang/Integer;)V");
    } else {
        LOGE("Failed to find AudioQuality class");
    }
    
    jclass localArrayListClass = env->FindClass("java/util/ArrayList");
    if (localArrayListClass != nullptr) {
        arrayListClass = (jclass)env->NewGlobalRef(localArrayListClass);
        env->DeleteLocalRef(localArrayListClass);
        
        // ArrayList 생성자 및 add 메서드 참조 가져오기
        arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
        arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    } else {
        LOGE("Failed to find ArrayList class");
    }
    
    jclass localHashMapClass = env->FindClass("java/util/HashMap");
    if (localHashMapClass != nullptr) {
        hashMapClass = (jclass)env->NewGlobalRef(localHashMapClass);
        env->DeleteLocalRef(localHashMapClass);
        
        // HashMap 생성자 및 put 메서드 참조 가져오기
        hashMapConstructor = env->GetMethodID(hashMapClass, "<init>", "()V");
        hashMapPut = env->GetMethodID(hashMapClass, "put", 
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    } else {
        LOGE("Failed to find HashMap class");
    }
}

JNIEnv* JNIBridge::getEnv() {
    if (javaVM == nullptr) {
        LOGE("JavaVM is not set");
        return nullptr;
    }
    
    JNIEnv* env;
    jint result = javaVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    
    if (result == JNI_EDETACHED) {
        LOGI("Thread not attached, attaching...");
        if (javaVM->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            LOGE("Failed to attach thread to JavaVM");
            return nullptr;
        }
    } else if (result != JNI_OK) {
        LOGE("Failed to get JNI environment");
        return nullptr;
    }
    
    return env;
}

jstring JNIBridge::toJavaString(JNIEnv* env, const std::string& str) {
    if (env == nullptr) {
        LOGE("Invalid JNI environment in toJavaString");
        return nullptr;
    }
    
    return env->NewStringUTF(str.c_str());
}

std::string JNIBridge::toString(JNIEnv* env, jstring jstr) {
    if (env == nullptr || jstr == nullptr) {
        LOGE("Invalid parameters in toString");
        return "";
    }
    
    const char* str = env->GetStringUTFChars(jstr, nullptr);
    if (str == nullptr) {
        LOGE("Failed to get string UTF chars");
        return "";
    }
    
    std::string result(str);
    env->ReleaseStringUTFChars(jstr, str);
    
    return result;
}

jobject JNIBridge::toJavaAudioQuality(JNIEnv* env, const AudioQuality& quality) {
    if (env == nullptr || audioQualityClass == nullptr || audioQualityConstructor == nullptr) {
        LOGE("Invalid environment or class references in toJavaAudioQuality");
        return nullptr;
    }
    
    jstring formatStr = toJavaString(env, quality.format);
    
    // null for bitrate parameter
    jobject audioQualityObj = env->NewObject(audioQualityClass, audioQualityConstructor,
        quality.sampleRate, quality.bitDepth, formatStr, quality.channels, nullptr);
    
    env->DeleteLocalRef(formatStr);
    
    return audioQualityObj;
}

jobject JNIBridge::toJavaTrack(JNIEnv* env, const TrackMetadata& metadata) {
    if (env == nullptr || trackClass == nullptr || trackConstructor == nullptr) {
        LOGE("Invalid environment or class references in toJavaTrack");
        return nullptr;
    }
    
    jstring idStr = toJavaString(env, metadata.id);
    jstring titleStr = toJavaString(env, metadata.title);
    jstring artistStr = toJavaString(env, metadata.artist);
    jstring albumStr = toJavaString(env, metadata.albumTitle);  // album -> albumTitle로 수정
    jstring genreStr = toJavaString(env, metadata.genre);
    jstring pathStr = toJavaString(env, metadata.filePath);
    jlong duration = static_cast<jlong>(metadata.duration);     // durationMs -> duration으로 수정
    jint playCount = static_cast<jint>(metadata.playCount);
    jint year = static_cast<jint>(metadata.year);
    jint trackNumber = static_cast<jint>(metadata.trackNumber);
    
    // AudioQuality 객체 생성
    jobject audioQualityObj = toJavaAudioQuality(env, metadata.audioQuality);  // quality -> audioQuality로 수정
    
    // Track 객체 생성
    jstring composerStr = toJavaString(env, metadata.composer);
    jstring albumArtPathStr = toJavaString(env, metadata.albumArtPath);
    
    // Java Long 객체 생성 (lastPlayed)
    jclass longClass = env->FindClass("java/lang/Long");
    jmethodID longConstructor = env->GetMethodID(longClass, "<init>", "(J)V");
    jobject lastPlayedObj = env->NewObject(longClass, longConstructor, static_cast<jlong>(metadata.lastPlayed));
    
    // Java Integer 객체 생성 (year)
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");
    jobject yearObj = env->NewObject(integerClass, integerConstructor, year);
    
    // Java Integer 객체 생성 (trackNumber)
    jobject trackNumberObj = env->NewObject(integerClass, integerConstructor, trackNumber);
    
    // Track 객체 생성
    jobject trackObj = env->NewObject(trackClass, trackConstructor,
        idStr, titleStr, artistStr, albumStr, albumArtPathStr,
        duration, pathStr, audioQualityObj, genreStr, yearObj, trackNumberObj, 
        composerStr, playCount, lastPlayedObj);
    
    // 로컬 참조 정리
    env->DeleteLocalRef(composerStr);
    env->DeleteLocalRef(albumArtPathStr);
    env->DeleteLocalRef(longClass);
    env->DeleteLocalRef(lastPlayedObj);
    env->DeleteLocalRef(integerClass);
    env->DeleteLocalRef(yearObj);
    env->DeleteLocalRef(trackNumberObj);
    
    // 로컬 참조 정리
    env->DeleteLocalRef(idStr);
    env->DeleteLocalRef(titleStr);
    env->DeleteLocalRef(artistStr);
    env->DeleteLocalRef(albumStr);
    env->DeleteLocalRef(genreStr);
    env->DeleteLocalRef(pathStr);
    env->DeleteLocalRef(audioQualityObj);
    
    return trackObj;
}

jobject JNIBridge::toJavaTrackList(JNIEnv* env, const std::vector<TrackMetadata>& tracks) {
    if (env == nullptr || arrayListClass == nullptr || arrayListConstructor == nullptr || arrayListAdd == nullptr) {
        LOGE("Invalid environment or class references in toJavaTrackList");
        return nullptr;
    }
    
    // ArrayList 생성
    jobject arrayListObj = env->NewObject(arrayListClass, arrayListConstructor);
    if (arrayListObj == nullptr) {
        LOGE("Failed to create ArrayList in toJavaTrackList");
        return nullptr;
    }
    
    // 각 트랙을 Track 객체로 변환하여 ArrayList에 추가
    for (const auto& track : tracks) {
        jobject trackObj = toJavaTrack(env, track);
        if (trackObj != nullptr) {
            env->CallBooleanMethod(arrayListObj, arrayListAdd, trackObj);
            env->DeleteLocalRef(trackObj);
        }
    }
    
    return arrayListObj;
}

jobject JNIBridge::toJavaGenreTrackMap(JNIEnv* env, const std::map<std::string, std::vector<TrackMetadata>>& genreTracks) {
    if (env == nullptr || hashMapClass == nullptr || hashMapConstructor == nullptr || hashMapPut == nullptr) {
        LOGE("Invalid environment or class references in toJavaGenreTrackMap");
        return nullptr;
    }
    
    // HashMap 생성
    jobject hashMapObj = env->NewObject(hashMapClass, hashMapConstructor);
    if (hashMapObj == nullptr) {
        LOGE("Failed to create HashMap in toJavaGenreTrackMap");
        return nullptr;
    }
    
    // 각 장르와 트랙 리스트를 HashMap에 추가
    for (const auto& entry : genreTracks) {
        jstring genreStr = toJavaString(env, entry.first);
        jobject trackListObj = toJavaTrackList(env, entry.second);
        
        if (genreStr != nullptr && trackListObj != nullptr) {
            env->CallObjectMethod(hashMapObj, hashMapPut, genreStr, trackListObj);
            env->DeleteLocalRef(genreStr);
            env->DeleteLocalRef(trackListObj);
        }
    }
    
    return hashMapObj;
}

// JNI 네이티브 메서드 구현
extern "C" {
// 디렉토리 스캔
JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeScanDirectory(
        JNIEnv* env, jobject thiz, jstring directoryPath) {

    const char* path = env->GetStringUTFChars(directoryPath, 0);

    auto progressCallback = [](int current, int total) {
        // 필요시 프로그레스 콜백 구현
    };

    bool result = AudioScanner::getInstance().scanDirectory(path, progressCallback);

    env->ReleaseStringUTFChars(directoryPath, path);

    return result ? JNI_TRUE : JNI_FALSE;
}

// 파일 스캔
JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeScanFile(
        JNIEnv* env, jobject thiz, jstring filePath) {

    const char* path = env->GetStringUTFChars(filePath, 0);
    bool result = AudioScanner::getInstance().scanFile(path);
    env->ReleaseStringUTFChars(filePath, path);

    return result ? JNI_TRUE : JNI_FALSE;
}

// 트랙 ID로 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetTrackById(
        JNIEnv* env, jobject thiz, jstring trackId) {

    const char* id = env->GetStringUTFChars(trackId, 0);
    TrackMetadata metadata = AudioScanner::getInstance().getTrackById(id);
    env->ReleaseStringUTFChars(trackId, id);

    // JNIBridge의 변환 메서드 사용
    return JNIBridge::toJavaTrack(env, metadata);
}

// 모든 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetAllTracks(
        JNIEnv* env, jobject thiz) {

    std::vector<TrackMetadata> tracks = AudioScanner::getInstance().getAllTracks();
    return JNIBridge::toJavaTrackList(env, tracks);
}

// 장르별 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetTracksByGenre(
        JNIEnv* env, jobject thiz) {

    std::map<std::string, std::vector<TrackMetadata>> genreTracks =
            AudioScanner::getInstance().getTracksByGenre();
    return JNIBridge::toJavaGenreTrackMap(env, genreTracks);
}

// 최근 재생 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetRecentlyPlayedTracks(
        JNIEnv* env, jobject thiz, jint limit) {

    std::vector<TrackMetadata> tracks =
            AudioScanner::getInstance().getRecentlyPlayedTracks(limit);
    return JNIBridge::toJavaTrackList(env, tracks);
}

// 자주 재생된 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetFrequentlyPlayedTracks(
        JNIEnv* env, jobject thiz, jint limit) {

    std::vector<TrackMetadata> tracks =
            AudioScanner::getInstance().getFrequentlyPlayedTracks(limit);
    return JNIBridge::toJavaTrackList(env, tracks);
}

// 최근 추가된 트랙 가져오기
JNIEXPORT jobject JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeGetRecentlyAddedTracks(
        JNIEnv* env, jobject thiz, jint limit) {

    std::vector<TrackMetadata> tracks =
            AudioScanner::getInstance().getRecentlyAddedTracks(limit);
    return JNIBridge::toJavaTrackList(env, tracks);
}

// 재생 횟수 업데이트
JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeUpdatePlayCount(
        JNIEnv* env, jobject thiz, jstring trackId) {

    const char* id = env->GetStringUTFChars(trackId, 0);
    AudioScanner::getInstance().updatePlayCount(id);
    env->ReleaseStringUTFChars(trackId, id);
}

// 마지막 재생 시간 업데이트
JNIEXPORT void JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeUpdateLastPlayed(
        JNIEnv* env, jobject thiz, jstring trackId) {

    const char* id = env->GetStringUTFChars(trackId, 0);
    AudioScanner::getInstance().updateLastPlayed(id);
    env->ReleaseStringUTFChars(trackId, id);
}

// 데이터베이스 저장
JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeSaveDatabase(
        JNIEnv* env, jobject thiz, jstring dbFilePath) {

    const char* path = env->GetStringUTFChars(dbFilePath, 0);
    bool result = AudioScanner::getInstance().saveDatabase(path);
    env->ReleaseStringUTFChars(dbFilePath, path);

    return result ? JNI_TRUE : JNI_FALSE;
}

// 데이터베이스 로드
JNIEXPORT jboolean JNICALL
Java_com_example_pancakemusicbox_audio_AudioScannerNative_nativeLoadDatabase(
        JNIEnv* env, jobject thiz, jstring dbFilePath) {

    const char* path = env->GetStringUTFChars(dbFilePath, 0);
    bool result = AudioScanner::getInstance().loadDatabase(path);
    env->ReleaseStringUTFChars(dbFilePath, path);

    return result ? JNI_TRUE : JNI_FALSE;
}
}