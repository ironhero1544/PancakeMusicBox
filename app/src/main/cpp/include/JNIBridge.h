#ifndef PANCAKEMUSICBOX_JNIBRIDGE_H
#define PANCAKEMUSICBOX_JNIBRIDGE_H

#include <jni.h>
#include <string>
#include <vector>
#include "AudioMetadata.h"

namespace pancakemusicbox {

/**
 * JNI와 C++ 코드 간의 데이터 변환을 위한 유틸리티 클래스
 */
class JNIBridge {
public:
    // Java 환경 세팅
    static void setJavaVM(JavaVM* vm);
    
    // JNI 환경 얻기
    static JNIEnv* getEnv();
    
    // C++ 문자열을 Java 문자열로 변환
    static jstring toJavaString(JNIEnv* env, const std::string& str);
    
    // Java 문자열을 C++ 문자열로 변환
    static std::string toString(JNIEnv* env, jstring jstr);
    
    // C++ TrackMetadata를 Java Track 객체로 변환
    static jobject toJavaTrack(JNIEnv* env, const TrackMetadata& metadata);
    
    // C++ AudioQuality를 Java AudioQuality 객체로 변환
    static jobject toJavaAudioQuality(JNIEnv* env, const AudioQuality& quality);
    
    // C++ 트랙 벡터를 Java 트랙 ArrayList로 변환
    static jobject toJavaTrackList(JNIEnv* env, const std::vector<TrackMetadata>& tracks);
    
    // C++ 장르별 트랙 맵을 Java Map으로 변환
    static jobject toJavaGenreTrackMap(JNIEnv* env, const std::map<std::string, std::vector<TrackMetadata>>& genreTracks);
    
private:
    // JavaVM 인스턴스
    static JavaVM* javaVM;
    
    // Java 클래스 캐시
    static jclass trackClass;
    static jclass audioQualityClass;
    static jclass arrayListClass;
    static jclass hashMapClass;
    
    // Java 메서드 ID 캐시
    static jmethodID trackConstructor;
    static jmethodID audioQualityConstructor;
    static jmethodID arrayListConstructor;
    static jmethodID arrayListAdd;
    static jmethodID hashMapConstructor;
    static jmethodID hashMapPut;
};

} // namespace pancakemusicbox

#endif // PANCAKEMUSICBOX_JNIBRIDGE_H
