package com.example.pancakemusicbox

import android.app.Application
import timber.log.Timber

/**
 * HiFi Player 애플리케이션 클래스
 * 전체 애플리케이션 컨텍스트를 관리하며, 앱의 시작점에서 필요한 초기화를 담당
 */
class HiFiPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // 로깅 초기화 - 디버그 모드에서만 로깅
        Timber.plant(Timber.DebugTree())
        
        // 캐시 디렉토리 설정
        createCacheDirectories()
    }
    
    /**
     * 앱이 사용할 캐시 디렉토리들을 생성
     */
    private fun createCacheDirectories() {
        // 앨범 아트 캐시 디렉토리
        val albumArtCacheDir = cacheDir.resolve("album_art")
        if (!albumArtCacheDir.exists()) {
            albumArtCacheDir.mkdirs()
        }
        
        // 오디오 분석 캐시 디렉토리
        val audioAnalysisCacheDir = cacheDir.resolve("audio_analysis")
        if (!audioAnalysisCacheDir.exists()) {
            audioAnalysisCacheDir.mkdirs()
        }
    }
}