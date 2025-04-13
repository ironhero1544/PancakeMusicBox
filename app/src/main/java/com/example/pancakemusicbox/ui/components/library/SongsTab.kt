package com.example.pancakemusicbox.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.ui.theme.HighResAudio

/**
 * 노래 탭의 트랙 리스트 아이템
 */
@Composable
fun SongListItem(
    track: Track,
    isCurrentlyPlaying: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 앨범 아트
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 Coil로 이미지 로드
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 트랙 정보
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 트랙 제목 (하이레스 표시 포함)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (track.isHighRes) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(HighResAudio)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                Text(
                    text = track.getTitle(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 아티스트 & 앨범
            Text(
                text = "${track.getArtist()} • ${track.getAlbumTitle()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 트랙 길이 (분:초 형식)
        Text(
            text = formatDuration(track.getDuration()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 더보기 버튼
        Icon(
            painter = painterResource(id = R.drawable.ic_more_vert),
            contentDescription = "More Options",
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onMoreClick),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

/**
 * 노래 탭 콘텐츠
 */
@Composable
fun SongsTab(
    tracks: List<Track>,
    currentlyPlayingTrackId: String?,
    onTrackClick: (Track) -> Unit,
    onTrackMoreClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(tracks) { track ->
            SongListItem(
                track = track,
                isCurrentlyPlaying = track.getId() == currentlyPlayingTrackId,
                onClick = { onTrackClick(track) },
                onMoreClick = { onTrackMoreClick(track) }
            )
            
            Divider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 80.dp, end = 16.dp)
            )
        }
        
        // 하단 여백
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 밀리초를 "분:초" 형식으로 변환
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
