package com.example.pancakemusicbox.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.ui.theme.HighResAudio
import com.example.pancakemusicbox.ui.theme.OverlayGradientEnd
import com.example.pancakemusicbox.ui.theme.OverlayGradientStart

/**
 * 최근 재생 섹션의 트랙 카드 컴포넌트
 */
@Composable
fun RecentTrackCard(
    track: Track,
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Box {
            // 앨범 아트 (실제로는 Coil로 로드)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 그라데이션 오버레이
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(OverlayGradientStart, OverlayGradientEnd),
                            startY = 0f,
                            endY = 600f
                        )
                    )
            )
            
            // 트랙 정보 (하단)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // 오디오 품질 정보
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (track.isHighRes) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(HighResAudio)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    
                    Text(
                        text = track.getAudioQuality().getAudioQualityString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // 트랙 제목
                Text(
                    text = track.getTitle(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 아티스트 이름
                Text(
                    text = track.getArtist(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 재생 버튼 (우측 하단)
            IconButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 최근 재생 섹션 컴포넌트 (페이징)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentlyPlayedSection(
    tracks: List<Track>,
    currentlyPlayingTrackId: String? = null,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 헤더
        Text(
            text = "최근 재생",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 페이징 내용
        if (tracks.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { tracks.size })
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val track = tracks[page]
                RecentTrackCard(
                    track = track,
                    isPlaying = track.getId() == currentlyPlayingTrackId,
                    onPlayClick = { onTrackClick(track) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 페이지 인디케이터
            PageIndicator(
                pagerState = pagerState,
                pageCount = tracks.size,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )
        } else {
            // 최근 재생 항목이 없는 경우
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "최근 재생한 트랙이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * 페이지 인디케이터 컴포넌트
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == pagerState.currentPage
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        width = if (isSelected) 16.dp else 8.dp,
                        height = 8.dp
                    )
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
