package com.example.pancakemusicbox.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// HIFI 오디오 관련 추가 색상 (전체 앱에서 사용)
val HighResAudio: Color = Color(0xFFFFD700)    // 금색 (하이레스 오디오 표시용)
val MiniPlayerBackground: Color = Color(0xFF0D1117)  // 조금 더 어두운 배경색 (미니 플레이어 배경)

// 다크 모드 색상 스키마
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C8AFF),       // 밝은 블루
    onPrimary = Color(0xFFFFFFFF),     // 화이트
    primaryContainer = Color(0xFF304FA6), // 딥 블루
    onPrimaryContainer = Color(0xFFDDE5FF), // 라이트 블루
    secondary = Color(0xFF9376E0),      // 퍼플
    onSecondary = Color(0xFFFFFFFF),    // 화이트
    secondaryContainer = Color(0xFF7558BA), // 딥 퍼플
    onSecondaryContainer = Color(0xFFEFE4FF), // 라이트 퍼플
    background = Color(0xFF121212),     // 다크 그레이
    onBackground = Color(0xFFE4E4E4),   // 라이트 그레이
    surface = Color(0xFF1E1E1E),        // 라이트 다크 그레이
    onSurface = Color(0xFFEEEEEE),      // 오프 화이트
    error = Color(0xFFCF6679),          // 에러 레드
    onError = Color(0xFFFFFFFF)         // 화이트
)

// 라이트 모드 색상 스키마 (다크 모드 기준으로 하는 앱이므로 최소한만 구현)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),     // 인디고
    onPrimary = Color(0xFFFFFFFF),     // 화이트
    primaryContainer = Color(0xFFD1D9FF),     // 라이트 인디고
    onPrimaryContainer = Color(0xFF0A1A70),     // 다크 인디고
    secondary = Color(0xFF673AB7),     // 퍼플
    onSecondary = Color(0xFFFFFFFF),     // 화이트
    secondaryContainer = Color(0xFFEDE0FF),     // 라이트 퍼플
    onSecondaryContainer = Color(0xFF33004B),     // 다크 퍼플
    background = Color(0xFFFAFAFA),     // 화이트
    onBackground = Color(0xFF212121),     // 다크 그레이
    surface = Color(0xFFFFFFFF),     // 화이트
    onSurface = Color(0xFF212121),     // 다크 그레이
    error = Color(0xFFB00020),     // 에러 레드
    onError = Color(0xFFFFFFFF)      // 화이트
)

// 타이포그래피 설정
private val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // 다른 스타일 생략
)

@Composable
fun HiFiPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}