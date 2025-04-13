package com.example.pancakemusicbox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.ui.components.player.AlbumArt
import com.example.pancakemusicbox.ui.components.player.AudioSpectrum
import com.example.pancakemusicbox.ui.components.player.PlaybackControls
import com.example.pancakemusicbox.ui.components.player.PlayerTabContent
import com.example.pancakemusicbox.ui.components.player.SeekBar
import com.example.pancakemusicbox.viewmodel.AudioPlayerViewModel
import com.example.pancakemusicbox.viewmodel.MusicViewModel

/**
 * 전체 플레이어 화면
 */
@Composable
fun PlayerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    audioViewModel: AudioPlayerViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel()
) {
    // AudioViewModel에서 재생 관련 상태 얻기
    val currentTrack by audioViewModel.currentTrack.observeAsState()
    val isPlaying by audioViewModel.isPlaying.observeAsState(false)
    val currentPositionMs by audioViewModel.currentPosition.observeAsState(0L)
    val durationMs by audioViewModel.duration.observeAsState(0L)
    val visualizationData by audioViewModel.visualizationData.observeAsState(FloatArray(20) { 0f })
    val isShuffleEnabled by audioViewModel.isShuffleEnabled.observeAsState(false)
    val repeatMode by audioViewModel.repeatMode.observeAsState(0)
    val playlist by audioViewModel.playlist.observeAsState(emptyList())
    val currentTrackIndex by audioViewModel.currentTrackIndex.observeAsState(0)
    
    // MusicViewModel에서 트랙 데이터 얻기
    val allTracks by musicViewModel.getAllTracks().observeAsState(emptyList())
    
    // 현재 선택된 탭
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // 가사 데이터 (샘플)
    val lyrics = remember {
        if (currentTrack?.getTitle()?.contains("Bohemian") == true) {
            listOf(
                0L to "Is this the real life?",
                5000L to "Is this just fantasy?",
                10000L to "Caught in a landslide,",
                15000L to "No escape from reality",
                20000L to "Open your eyes,",
                25000L to "Look up to the skies and see",
                30000L to "I'm just a poor boy, I need no sympathy",
                35000L to "Because I'm easy come, easy go,",
                40000L to "Little high, little low",
                45000L to "Any way the wind blows doesn't really matter to me"
            )
        } else {
            null
        }
    }
    
    // 트랙 종료 감지 및 처리
    LaunchedEffect(currentPositionMs, durationMs) {
        if (durationMs > 0 && currentPositionMs >= durationMs) {
            // 트랙 재생 완료 처리
            audioViewModel.onTrackCompletion()
        }
    }
    
    // 초기 데이터 로드 (아직 데이터가 없는 경우)
    LaunchedEffect(allTracks) {
        if (currentTrack == null && playlist.isEmpty() && allTracks.isNotEmpty()) {
            audioViewModel.setPlaylist(allTracks)
            audioViewModel.loadTrack(0, true)
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 바 (뒤로가기, 트랙 제목, 더보기)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 뒤로가기 버튼
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_placeholder), // 뒤로가기 아이콘
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // 트랙 제목
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentTrack?.getTitle() ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Text(
                        text = currentTrack?.getArtist() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                
                // 더보기 버튼
                IconButton(
                    onClick = { /* 더보기 메뉴 표시 */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_vert),
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            // 앨범 아트
            currentTrack?.let { track ->
                AlbumArt(
                    track = track,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                )
            }
            
            // 오디오 시각화
            if (isPlaying) {
                // remember 함수 대신 일반 컴파일타임 변환 사용
                val audioDataList = visualizationData.toList()
                AudioSpectrum(
                    audioData = audioDataList,
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }
            
            // 시크바 및 시간 정보
            currentTrack?.let { track ->
                SeekBar(
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onSeek = { newPosition ->
                        audioViewModel.seekTo(newPosition)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // 재생 컨트롤
            PlaybackControls(
                isPlaying = isPlaying,
                onPrevious = { audioViewModel.playPrevious() },
                onNext = { audioViewModel.playNext() },
                onPlayPause = { audioViewModel.togglePlayback() },
                onShuffle = { audioViewModel.toggleShuffle() },
                onRepeat = { audioViewModel.toggleRepeatMode() },
                isShuffleOn = isShuffleEnabled,
                repeatMode = repeatMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 하단 탭 (가사, 설정, 재생목록)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("가사") }
                )
                
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("설정") }
                )
                
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("재생목록") }
                )
            }
            
            // 탭 내용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // 탭 콘텐츠 표시
                currentTrack?.let { track ->
                    PlayerTabContent(
                        tabIndex = selectedTabIndex,
                        track = track,
                        currentPositionMs = currentPositionMs,
                        audioViewModel = audioViewModel,
                        lyrics = lyrics
                    )
                }
            }
        }
    }
}