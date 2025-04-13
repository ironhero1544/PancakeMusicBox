package com.example.pancakemusicbox.audio;

import android.util.Log;

import com.example.pancakemusicbox.model.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 오디오 파일 스캐닝 및 메타데이터 추출을 위한 네이티브 인터페이스
 * C++ 코드와 JNI를 통해 연결됨
 */
public class AudioScannerNative {
    private static final String TAG = "AudioScannerNative";

    // 프로그레스 콜백 리스너 인터페이스
    public interface ScanProgressListener {
        void onProgress(int current, int total);
    }

    // 싱글톤 인스턴스
    private static AudioScannerNative instance;
    
    // 프로그레스 리스너
    private static ScanProgressListener progressListener;

    // 생성자 (비공개)
    private AudioScannerNative() {
        try {
            // 네이티브 라이브러리 로드
            System.loadLibrary("pancakemusicbox");
            Log.d(TAG, "Native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native library: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 싱글톤 인스턴스 얻기
    public static synchronized AudioScannerNative getInstance() {
        if (instance == null) {
            instance = new AudioScannerNative();
        }
        return instance;
    }

    // 프로그레스 리스너 설정
    public void setProgressListener(ScanProgressListener listener) {
        progressListener = listener;
    }

    // 네이티브 코드에서 호출하는 프로그레스 콜백 메서드
    public static void onScanProgress(int current, int total) {
        if (progressListener != null) {
            progressListener.onProgress(current, total);
        }
    }

    // 디렉토리 스캔
    public boolean scanDirectory(String directoryPath) {
        Log.d(TAG, "Scanning directory: " + directoryPath);
        
        // 디렉토리 유효성 검사
        if (directoryPath == null || directoryPath.isEmpty()) {
            Log.e(TAG, "Invalid directory path (null or empty)");
            return false;
        }
        
        // 디렉토리 존재 확인
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            Log.e(TAG, "Directory does not exist: " + directoryPath);
            return false;
        }
        
        // 디렉토리 접근 권한 확인
        if (!dir.canRead()) {
            Log.e(TAG, "No read permission for directory: " + directoryPath);
            return false;
        }
        
        try {
            return nativeScanDirectory(directoryPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native library error scanning directory: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error scanning directory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 파일 스캔
    public boolean scanFile(String filePath) {
        Log.d(TAG, "Scanning file: " + filePath);
        try {
            return nativeScanFile(filePath);
        } catch (Exception e) {
            Log.e(TAG, "Error scanning file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ID로 트랙 가져오기
    public Track getTrackById(String trackId) {
        try {
            return nativeGetTrackById(trackId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting track by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // 모든 트랙 가져오기
    public List<Track> getAllTracks() {
        try {
            return nativeGetAllTracks();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all tracks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 장르별 트랙 가져오기
    public Map<String, List<Track>> getTracksByGenre() {
        try {
            return nativeGetTracksByGenre();
        } catch (Exception e) {
            Log.e(TAG, "Error getting tracks by genre: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // 최근 재생 트랙 가져오기
    public List<Track> getRecentlyPlayedTracks(int limit) {
        try {
            return nativeGetRecentlyPlayedTracks(limit);
        } catch (Exception e) {
            Log.e(TAG, "Error getting recently played tracks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 자주 재생된 트랙 가져오기
    public List<Track> getFrequentlyPlayedTracks(int limit) {
        try {
            return nativeGetFrequentlyPlayedTracks(limit);
        } catch (Exception e) {
            Log.e(TAG, "Error getting frequently played tracks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 최근 추가된 트랙 가져오기
    public List<Track> getRecentlyAddedTracks(int limit) {
        try {
            return nativeGetRecentlyAddedTracks(limit);
        } catch (Exception e) {
            Log.e(TAG, "Error getting recently added tracks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 재생 횟수 업데이트
    public void updatePlayCount(String trackId) {
        try {
            nativeUpdatePlayCount(trackId);
        } catch (Exception e) {
            Log.e(TAG, "Error updating play count: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 마지막 재생 시간 업데이트
    public void updateLastPlayed(String trackId) {
        try {
            nativeUpdateLastPlayed(trackId);
        } catch (Exception e) {
            Log.e(TAG, "Error updating last played: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 데이터베이스 저장
    public boolean saveDatabase(String dbFilePath) {
        try {
            return nativeSaveDatabase(dbFilePath);
        } catch (Exception e) {
            Log.e(TAG, "Error saving database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 데이터베이스 로드
    public boolean loadDatabase(String dbFilePath) {
        try {
            return nativeLoadDatabase(dbFilePath);
        } catch (Exception e) {
            Log.e(TAG, "Error loading database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 네이티브 메소드 선언
    private native boolean nativeScanDirectory(String directoryPath);
    private native boolean nativeScanFile(String filePath);
    private native Track nativeGetTrackById(String trackId);
    private native List<Track> nativeGetAllTracks();
    private native Map<String, List<Track>> nativeGetTracksByGenre();
    private native List<Track> nativeGetRecentlyPlayedTracks(int limit);
    private native List<Track> nativeGetFrequentlyPlayedTracks(int limit);
    private native List<Track> nativeGetRecentlyAddedTracks(int limit);
    private native void nativeUpdatePlayCount(String trackId);
    private native void nativeUpdateLastPlayed(String trackId);
    private native boolean nativeSaveDatabase(String dbFilePath);
    private native boolean nativeLoadDatabase(String dbFilePath);
}
