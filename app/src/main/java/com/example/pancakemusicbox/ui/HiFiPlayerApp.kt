package com.example.pancakemusicbox.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pancakemusicbox.ui.components.player.PlayerUI
import com.example.pancakemusicbox.ui.theme.HiFiPlayerTheme
import com.example.pancakemusicbox.viewmodel.AudioPlayerViewModel
import com.example.pancakemusicbox.viewmodel.MusicViewModel

/**
 * 앱의 메인 컴포저블 함수
 * HiFiPlayerTheme을 적용하고 PlayerUI를 호출합니다.
 */
@Composable
fun HiFiPlayerApp() {
    // ViewModels
    val musicViewModel: MusicViewModel = viewModel()
    val audioViewModel: AudioPlayerViewModel = viewModel()
    
    // NavController
    val navController = rememberNavController()
    
    // 공유 ViewModel 인스턴스 유지
    val rememberedAudioViewModel = remember { audioViewModel }
    val rememberedMusicViewModel = remember { musicViewModel }
    
    HiFiPlayerTheme {
        // PlayerUI를 통해 앱의 모든 UI 컴포넌트 구성
        PlayerUI(
            navController = navController,
            audioViewModel = rememberedAudioViewModel,
            musicViewModel = rememberedMusicViewModel
        )
    }
}
