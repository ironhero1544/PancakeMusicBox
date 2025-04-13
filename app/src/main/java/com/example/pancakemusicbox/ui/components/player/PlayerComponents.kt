package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.navigation.Screen
import com.example.pancakemusicbox.ui.components.BottomNavigationBar
import com.example.pancakemusicbox.ui.components.MiniPlayer
import com.example.pancakemusicbox.ui.screens.HomeScreen
import com.example.pancakemusicbox.ui.screens.LibraryScreen
import com.example.pancakemusicbox.ui.screens.PlayerScreen
import com.example.pancakemusicbox.ui.screens.SettingsScreen
import com.example.pancakemusicbox.utils.formatDuration
import com.example.pancakemusicbox.viewmodel.AudioPlayerViewModel
import com.example.pancakemusicbox.viewmodel.MusicViewModel
import timber.log.Timber

// 포맷팅 유틸리티 함수
fun formatTime(timeMs: Long): String {
    return formatDuration(timeMs)
}

/**
 * 앨범 아트 컴포넌트 (컴팩트 버전)
 */
@Composable
fun AlbumArtCompact(
    track: Track,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        // 실제로는 Coil로 이미지 로드
        Icon(
            painter = painterResource(id = R.drawable.ic_placeholder),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 시크바 컴포넌트
 */
@Composable
fun SeekBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
    var sliderPosition by remember { mutableStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }

    if (!isDragging) {
        sliderPosition = progress
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(currentPositionMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { newPosition ->
                    isDragging = true
                    sliderPosition = newPosition.coerceIn(0f, 1f)
                },
                onValueChangeFinished = {
                    isDragging = false
                    val newPositionMs = (sliderPosition * durationMs).toLong()
                    onSeek(newPositionMs)
                },
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 오디오 스펙트럼 시각화 컴포넌트 (샘플)
 */
@Composable
fun AudioSpectrum(
    audioData: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val barCount = 20
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / (barCount * 2f)

        for (i in 0 until barCount) {
            val amplitude = if (isPlaying && audioData.isNotEmpty()) {
                audioData[i % audioData.size]
            } else {
                0f
            }

            val barHeight = amplitude * canvasHeight
            val x = i * (barWidth * 2) + barWidth / 2
            val startY = canvasHeight / 2 - barHeight / 2
            val endY = canvasHeight / 2 + barHeight / 2

            drawLine(
                color = colorScheme.primary.copy(alpha = 0.6f),
                start = Offset(x, startY),
                end = Offset(x, endY),
                strokeWidth = barWidth * 0.8f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * PlayerUI - 모든 플레이어 관련 컴포넌트들을 구성하는 중앙 함수
 * 앱의 내비게이션 및 플레이어 UI를 관리합니다.
 */
@Composable
fun PlayerUI(
    navController: NavHostController,
    audioViewModel: AudioPlayerViewModel,
    musicViewModel: MusicViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showMiniPlayer = currentRoute != Screen.Player.route
    val showBottomNav = currentRoute != Screen.Player.route

    val currentTrack by audioViewModel.currentTrack.observeAsState()
    val isPlaying by audioViewModel.isPlaying.observeAsState(false)
    val currentPositionMs by audioViewModel.currentPosition.observeAsState(0L)
    val durationMs by audioViewModel.duration.observeAsState(0L)

    // Check track completion for auto-play next
    LaunchedEffect(key1 = currentPositionMs, key2 = durationMs) {
        try {
            if (durationMs > 0 && currentPositionMs >= durationMs) {
                audioViewModel.onTrackCompletion()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in track completion handling")
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                Column {
                    if (showMiniPlayer && currentTrack != null) {
                        MiniPlayer(
                            track = currentTrack!!,
                            isPlaying = isPlaying,
                            navController = navController,
                            onPlayPause = { audioViewModel.togglePlayback() },
                            onNext = { audioViewModel.playNext() }
                        )
                    }
                    BottomNavigationBar(navController = navController)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                // 홈 화면
                composable(Screen.Home.route) {
                    HomeScreen(
                        navController = navController,
                        viewModel = musicViewModel
                    )
                }
                // 라이브러리 화면
                composable(Screen.Library.route) {
                    LibraryScreen(
                        navController = navController,
                        viewModel = musicViewModel
                    )
                }
                // 설정 화면
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController = navController
                    )
                }
                // 전체 플레이어 화면
                composable(Screen.Player.route) {
                    PlayerScreen(
                        navController = navController,
                        audioViewModel = audioViewModel,
                        musicViewModel = musicViewModel
                    )
                }
                // 검색 화면 예시
                composable(Screen.Search.route) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Text(
                            text = "Search Screen (Coming Soon)",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                // 플레이리스트 상세 화면 예시
                composable(
                    route = "${Screen.PlaylistDetail.route}/{playlistId}",
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Text(
                            text = "Playlist Detail: $playlistId",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                // 앨범 상세 화면 예시
                composable(
                    route = "${Screen.AlbumDetail.route}/{albumId}",
                    arguments = listOf(
                        navArgument("albumId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Text(
                            text = "Album Detail: $albumId",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
