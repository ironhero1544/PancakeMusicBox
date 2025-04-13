package com.example.pancakemusicbox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pancakemusicbox.navigation.Screen
import com.example.pancakemusicbox.viewmodel.MusicViewModel
import com.example.pancakemusicbox.ui.components.HomeTopBar
import com.example.pancakemusicbox.ui.components.RecentlyAddedSection
import com.example.pancakemusicbox.ui.components.RecentlyPlayedSection
import com.example.pancakemusicbox.ui.components.FrequentlyPlayedSection
import com.example.pancakemusicbox.ui.components.UserPlaylistSection

/**
 * 홈 화면 
 */
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = viewModel()
) {
    // ViewModel에서 데이터 관찰
    val currentlyPlayingTrack by viewModel.getCurrentlyPlayingTrack().observeAsState()
    val recentlyPlayedTracks by viewModel.getRecentlyPlayedTracks().observeAsState(emptyList())
    val frequentlyPlayedByGenre by viewModel.getTracksByGenre().observeAsState(emptyMap())
    val recentlyAddedTracks by viewModel.getRecentlyAddedTracks().observeAsState(emptyList())
    val playlists by viewModel.getPlaylists().observeAsState(emptyList())
    
    // 재생 상태 (실제로는 AudioPlayerViewModel에서 관리)
    var isPlaying by remember { mutableStateOf(true) }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 앱바
            HomeTopBar(
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 메인 콘텐츠 (스크롤 가능)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 최근 재생 섹션
                    RecentlyPlayedSection(
                        tracks = recentlyPlayedTracks,
                        currentlyPlayingTrackId = currentlyPlayingTrack?.getId(),
                        onTrackClick = { track ->
                            // 트랙 재생
                            viewModel.playTrack(track)
                            isPlaying = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 자주 듣는 트랙 섹션 (장르별)
                    FrequentlyPlayedSection(
                        genreTracks = frequentlyPlayedByGenre,
                        onTrackClick = { track ->
                            // 트랙 재생
                            viewModel.playTrack(track)
                            isPlaying = true
                        },
                        onSeeMoreClick = { genre ->
                            // 장르별 모든 트랙 보기 화면으로 이동 (추후 구현)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                    
                    // 최근 추가된 음악 섹션
                    RecentlyAddedSection(
                        tracks = recentlyAddedTracks,
                        onTrackClick = { track ->
                            // 트랙 재생
                            viewModel.playTrack(track)
                            isPlaying = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                    
                    // 사용자 플레이리스트 섹션
                    UserPlaylistSection(
                        playlists = playlists,
                        onPlaylistClick = { playlist ->
                            // 플레이리스트 상세 화면으로 이동
                            navController.navigate(
                                Screen.Playlist.createRoute(playlist.getId())
                            )
                        },
                        onCreatePlaylistClick = {
                            // 새 플레이리스트 생성 다이얼로그 표시 (추후 구현)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )
                    
                    // 하단 여백 (미니플레이어 및 네비게이션 바 높이만큼)
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }
        }
    }
}