package com.example.pancakemusicbox.ui.theme

import androidx.compose.ui.graphics.Color

// Dark theme (primary theme as per requirements)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkPrimary = Color(0xFF9775FA)  // Accent color - purple tone
val DarkSecondary = Color(0xFF6E56B1)
val DarkOnBackground = Color(0xFFFFFFFF)  // White text on dark background
val DarkOnSurface = Color(0xFFE1E1E1)     // Light gray text for secondary information

// Text colors
val TextPrimary = Color(0xFFFFFFFF)       // Primary text - white
val TextSecondary = Color(0xFFAAAAAA)     // Secondary text - medium gray
val TextTertiary = Color(0xFF777777)      // Tertiary text - darker gray

// Action colors
val AccentPrimary = Color(0xFF9775FA)     // Primary accent color - purple
val AccentSecondary = Color(0xFF6E56B1)   // Secondary accent - darker purple

// Gradient colors
val GradientStart = Color(0xFF121212)     // Start with dark background
val GradientEnd = Color(0xFF000000)       // End with pure black
val OverlayGradientStart = Color(0x00000000)  // Transparent start for overlays
val OverlayGradientEnd = Color(0xCC000000)    // Semi-transparent black end for overlays

// Audio quality indicator colors
val StandardAudio = Color(0xFF9775FA)     // Regular accent for standard audio

// UI Element colors
val BottomNavBackground = Color(0xFF0A0A0A)  // Slightly darker than main background
// HighResAudio and MiniPlayerBackground now defined in HiFiPlayerTheme.kt
