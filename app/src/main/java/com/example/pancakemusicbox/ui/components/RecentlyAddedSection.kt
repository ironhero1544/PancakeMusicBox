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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 최근 추가된 트랙 아이템 그리드 컴포넌트
 */
@Composable
fun RecentlyAddedTrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // 앨범 아트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 Coil로 이미지 로드
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // 하이레스 인디케이터
            if (track.isHighRes) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(HighResAudio)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
        
        // 트랙 제목
        Text(
            text = track.getTitle(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
        )
        
        // 아티스트 이름
        Text(
            text = track.getArtist(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 최근 추가된 음악 섹션 컴포넌트
 */
@Composable
fun RecentlyAddedSection(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 헤더
        Text(
            text = "최근 추가",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 트랙 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            userScrollEnabled = false // 스크롤 중첩 방지
        ) {
            items(tracks) { track ->
                RecentlyAddedTrackItem(
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
        }
    }
}
