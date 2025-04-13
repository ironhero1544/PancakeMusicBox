package com.example.pancakemusicbox.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Playlist
import com.example.pancakemusicbox.ui.theme.AccentPrimary

/**
 * 플레이리스트 카드 컴포넌트
 */
@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // 플레이리스트 대표 이미지 (4개의 앨범 아트 조합)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
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
            
            // 플레이리스트 정보
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 플레이리스트 이름
                Text(
                    text = playlist.getName(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // 곡 수
                Text(
                    text = "${playlist.getTrackCount()}곡",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 새 플레이리스트 생성 카드 컴포넌트
 */
@Composable
fun CreatePlaylistCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                    painter = painterResource(id = R.drawable.ic_placeholder), // 실제로는 플러스 아이콘 사용
                    contentDescription = "Create Playlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 텍스트
            Text(
                text = "새 플레이리스트 만들기",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 사용자 플레이리스트 섹션 컴포넌트
 */
@Composable
fun UserPlaylistSection(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 헤더
        Text(
            text = "내 플레이리스트",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 플레이리스트 가로 스크롤 리스트
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 기존 플레이리스트들
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }
            
            // 새 플레이리스트 만들기 카드 (항상 마지막에 위치)
            item {
                CreatePlaylistCard(onClick = onCreatePlaylistClick)
            }
        }
    }
}
