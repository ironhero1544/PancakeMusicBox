package com.example.pancakemusicbox.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
 * 트랙 아이템 컴포넌트 (자주 듣는 트랙에서 사용)
 */
@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 앨범 아트 (작은 크기)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 Coil로 이미지 로드
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 트랙 정보
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 트랙 제목
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 아티스트 이름
            Text(
                text = track.getArtist(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // 음질 정보 (작은 텍스트)
        Text(
            text = track.getAudioQuality().getFormat(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

/**
 * 장르별 자주 듣는 트랙 섹션 컴포넌트
 */
@Composable
fun FrequentlyPlayedSection(
    genreTracks: Map<String, List<Track>>,
    onTrackClick: (Track) -> Unit,
    onSeeMoreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 헤더
        Text(
            text = "자주 듣는",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 장르별 리스트
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            genreTracks.forEach { (genre, tracks) ->
                item {
                    GenreTrackList(
                        genre = genre,
                        tracks = tracks,
                        onTrackClick = onTrackClick,
                        onSeeMoreClick = { onSeeMoreClick(genre) }
                    )
                }
            }
        }
    }
}

/**
 * 장르별 트랙 리스트 카드 컴포넌트
 */
@Composable
fun GenreTrackList(
    genre: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // 장르 타이틀
            Text(
                text = "자주 듣는 $genre",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            
            // 트랙 리스트
            Column {
                tracks.take(3).forEach { track ->
                    TrackListItem(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
            
            // 더 보기 버튼
            TextButton(
                onClick = onSeeMoreClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "더 보기",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}