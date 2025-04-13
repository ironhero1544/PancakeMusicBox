package com.example.pancakemusicbox.utils

import java.util.concurrent.TimeUnit

/**
 * 밀리초를 "분:초" 형식으로 변환
 */
fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - 
                 TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%d:%02d", minutes, seconds)
}

/**
 * 밀리초 단위 시간을 "hh:mm:ss" 형식으로 변환 (1시간 이상인 경우)
 */
fun formatDurationWithHours(durationMs: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) - 
                 TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - 
                 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}