package com.example.pancakemusicbox.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Playlist
import com.example.pancakemusicbox.ui.theme.AccentPrimary

/**
 * 플레이리스트 그리드 아이템 컴포넌트
 */
@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // 플레이리스트 대표 이미지 (4개의 앨범 아트 조합)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            val artworks = playlist.getRepresentativeArtworks(4)
            if (artworks.isNotEmpty()) {
                // 실제로는 Coil로 실제 이미지 로드
                // 여기서는 플레이스홀더로 대체
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(AccentPrimary.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (artworks.size > 0) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentPrimary
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(AccentPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (artworks.size > 2) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentPrimary
                                )
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(AccentPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (artworks.size > 1) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentPrimary
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(AccentPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (artworks.size > 3) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                // 아트워크가 없는 경우
                Icon(
                    painter = painterResource(id = R.drawable.ic_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 플레이리스트 이름
        Text(
            text = playlist.getName(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // 트랙 수
        Text(
            text = "${playlist.getTrackCount()}곡",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

/**
 * 새 플레이리스트 생성 버튼 컴포넌트
 */
@Composable
fun CreatePlaylistItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // 플러스 아이콘 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            // 플러스 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Create Playlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 텍스트
        Text(
            text = "새 플레이리스트 만들기",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 플레이리스트 탭 콘텐츠
 */
@Composable
fun PlaylistsTab(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // 플레이리스트 항목들
        items(playlists) { playlist ->
            PlaylistGridItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }
        
        // 새 플레이리스트 생성 항목
        item {
            CreatePlaylistItem(
                onClick = onCreatePlaylistClick
            )
        }
    }
}