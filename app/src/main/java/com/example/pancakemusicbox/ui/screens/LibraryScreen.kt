package com.example.pancakemusicbox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pancakemusicbox.navigation.Screen
import com.example.pancakemusicbox.viewmodel.MusicViewModel
import com.example.pancakemusicbox.ui.components.LibraryTopBar
import com.example.pancakemusicbox.ui.components.library.AlbumsTab
import com.example.pancakemusicbox.ui.components.library.ArtistsTab
import com.example.pancakemusicbox.ui.components.library.PlaylistsTab
import com.example.pancakemusicbox.ui.components.library.SongsTab
import com.example.pancakemusicbox.ui.components.library.extractAlbumsFromTracks
import com.example.pancakemusicbox.ui.components.library.extractArtistsFromTracks

/**
 * 라이브러리 화면
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // ViewModel에서 데이터 관찰
    val currentlyPlayingTrack by viewModel.getCurrentlyPlayingTrack().observeAsState()
    val allTracks by viewModel.getAllTracks().observeAsState(emptyList())
    val playlists by viewModel.getPlaylists().observeAsState(emptyList())
    
    // 아티스트 및 앨범 데이터 추출
    val artists = remember(allTracks) { extractArtistsFromTracks(allTracks) }
    val albums = remember(allTracks) { extractAlbumsFromTracks(allTracks) }
    
    // 탭 목록
    val tabs = listOf("노래", "플레이리스트", "아티스트", "앨범")
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 앱바
            LibraryTopBar(
                navController = navController,
                onImportClick = {
                    // 사용자가 지정한 경로 스캔
                    // 실제 앱에서는 디렉토리 선택 다이얼로그 표시
                    viewModel.scanDefaultMusicDirs()
                }
            )
            
            // 필터 탭 바
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            
            // 콘텐츠 영역 (선택된 탭에 따라 다른 내용 표시)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // 노래 탭
                        SongsTab(
                            tracks = allTracks,
                            currentlyPlayingTrackId = currentlyPlayingTrack?.getId(),
                            onTrackClick = { track ->
                                // 트랙 재생
                                viewModel.playTrack(track)
                            },
                            onTrackMoreClick = { track ->
                                // 트랙 메뉴 표시 (추후 구현)
                            }
                        )
                    }
                    1 -> {
                        // 플레이리스트 탭
                        PlaylistsTab(
                            playlists = playlists,
                            onPlaylistClick = { playlist ->
                                // 플레이리스트 상세 화면으로 이동
                                navController.navigate(
                                    Screen.Playlist.createRoute(playlist.getId())
                                )
                            },
                            onCreatePlaylistClick = {
                                // 새 플레이리스트 생성 다이얼로그 표시 (추후 구현)
                            }
                        )
                    }
                    2 -> {
                        // 아티스트 탭
                        ArtistsTab(
                            artists = artists,
                            onArtistClick = { artist ->
                                // 아티스트 상세 화면으로 이동
                                navController.navigate(
                                    Screen.ArtistDetail.createRoute(artist.getId())
                                )
                            }
                        )
                    }
                    3 -> {
                        // 앨범 탭
                        AlbumsTab(
                            albums = albums,
                            onAlbumClick = { album ->
                                // 앨범 상세 화면으로 이동
                                navController.navigate(
                                    Screen.AlbumDetail.createRoute(album.getId())
                                )
                            }
                        )
                    }
                }
            }
            
            // 하단 여백 (미니플레이어 및 네비게이션 바 높이만큼)
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}