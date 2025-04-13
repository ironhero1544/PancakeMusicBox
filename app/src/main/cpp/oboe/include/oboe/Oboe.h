#pragma once

#include <cstdint>
#include <string>
#include <vector>
#include <memory>
#include <functional>

namespace oboe {

// Oboe enums
enum class Direction { Output };
enum class PerformanceMode { LowLatency, None };
enum class SharingMode { Exclusive, Shared };
enum class AudioFormat { Float, I16, Unspecified };
enum class StreamState { Started, Stopped, Paused, Unknown };
enum class Result { OK, ErrorBase, ErrorDisconnected };

// Result to string
inline const char* convertToText(Result result) {
    switch (result) {
        case Result::OK: return "OK";
        case Result::ErrorBase: return "Error";
        case Result::ErrorDisconnected: return "Disconnected";
        default: return "Unknown";
    }
}

// Forward declarations
class AudioStream;

// Callback result
enum class DataCallbackResult { Continue, Stop };

// AudioStreamCallback interface
class AudioStreamCallback {
public:
    virtual ~AudioStreamCallback() = default;
    
    virtual DataCallbackResult onAudioReady(
        AudioStream *audioStream,
        void *audioData,
        int32_t numFrames) = 0;
    
    virtual void onErrorBeforeClose(AudioStream *audioStream, Result error) {}
    virtual void onErrorAfterClose(AudioStream *audioStream, Result error) {}
};

// AudioStream class
class AudioStream {
public:
    virtual ~AudioStream() = default;
    
    virtual Result requestStart() { 
        mState = StreamState::Started; 
        return Result::OK; 
    }
    
    virtual Result requestStop() { 
        mState = StreamState::Stopped; 
        return Result::OK; 
    }
    
    virtual Result requestPause() { 
        mState = StreamState::Paused; 
        return Result::OK; 
    }
    
    virtual Result close() { 
        mState = StreamState::Unknown; 
        return Result::OK; 
    }
    
    virtual StreamState getState() const { return mState; }
    
    virtual int getChannelCount() const { return mChannelCount; }
    virtual int getSampleRate() const { return mSampleRate; }
    
private:
    StreamState mState = StreamState::Unknown;
    int mChannelCount = 2;
    int mSampleRate = 44100;
    
    friend class AudioStreamBuilder;
};

// AudioStreamBuilder class
class AudioStreamBuilder {
public:
    AudioStreamBuilder* setDirection(Direction direction) {
        return this;
    }
    
    AudioStreamBuilder* setPerformanceMode(PerformanceMode mode) {
        return this;
    }
    
    AudioStreamBuilder* setSharingMode(SharingMode mode) {
        return this;
    }
    
    AudioStreamBuilder* setFormat(AudioFormat format) {
        return this;
    }
    
    AudioStreamBuilder* setChannelCount(int channelCount) {
        mChannelCount = channelCount;
        return this;
    }
    
    AudioStreamBuilder* setSampleRate(int sampleRate) {
        mSampleRate = sampleRate;
        return this;
    }
    
    AudioStreamBuilder* setCallback(AudioStreamCallback* callback) {
        mCallback = callback;
        return this;
    }
    
    Result openStream(std::shared_ptr<AudioStream>& stream) {
        stream = std::make_shared<AudioStream>();
        stream->mChannelCount = mChannelCount;
        stream->mSampleRate = mSampleRate;
        return Result::OK;
    }
    
private:
    int mChannelCount = 2;
    int mSampleRate = 44100;
    AudioStreamCallback* mCallback = nullptr;
};

} // namespace oboe
