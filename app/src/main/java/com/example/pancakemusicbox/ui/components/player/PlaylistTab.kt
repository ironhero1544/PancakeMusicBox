package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.utils.formatDuration

/**
 * 재생목록 탭 컴포넌트
 * @param currentTrack 현재 재생 중인 트랙
 * @param queue 재생 대기열
 * @param currentTrackIndex 현재 재생 중인 트랙 인덱스
 * @param onTrackSelect 트랙 선택 이벤트
 * @param onMoveTrack 트랙 순서 변경 이벤트
 * @param onSaveQueue 현재 재생목록 저장 이벤트
 * @param onClearQueue 재생목록 비우기 이벤트
 * @param onShuffleQueue 재생목록 셔플 이벤트
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistTab(
    currentTrack: Track,
    queue: List<Track>,
    currentTrackIndex: Int,
    onTrackSelect: (Int) -> Unit,
    onMoveTrack: (Int, Int) -> Unit,
    onSaveQueue: () -> Unit,
    onClearQueue: () -> Unit,
    onShuffleQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 재생목록 헤더 및 컨트롤
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "현재 재생목록",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 셔플 버튼
            IconButton(onClick = onShuffleQueue) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shuffle),
                    contentDescription = "Shuffle Queue",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 저장 버튼
            IconButton(onClick = onSaveQueue) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Save Queue",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 초기화 버튼
            IconButton(onClick = onClearQueue) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vert),
                    contentDescription = "Clear Queue",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (queue.isEmpty()) {
            // 재생목록이 비어있는 경우
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "재생목록이 비어있습니다",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            // 재생목록 트랙 리스트
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 현재 재생 중인 트랙
                if (currentTrackIndex >= 0 && currentTrackIndex < queue.size) {
                    item {
                        Text(
                            text = "현재 재생 중",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        QueueItem(
                            track = currentTrack,
                            isCurrentTrack = true,
                            index = currentTrackIndex,
                            onTrackClick = { onTrackSelect(currentTrackIndex) }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (currentTrackIndex < queue.size - 1) {
                            Text(
                                text = "다음 트랙",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                
                // 다음 재생 트랙들
                itemsIndexed(
                    items = queue.subList(
                        fromIndex = (currentTrackIndex + 1).coerceAtMost(queue.size),
                        toIndex = queue.size
                    )
                ) { relativeIndex, track ->
                    val absoluteIndex = currentTrackIndex + 1 + relativeIndex
                    
                    QueueItem(
                        track = track,
                        isCurrentTrack = false,
                        index = absoluteIndex,
                        onTrackClick = { onTrackSelect(absoluteIndex) }
                    )
                    
                    if (relativeIndex < queue.size - currentTrackIndex - 2) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        )
                    }
                }
                
                // 이전 트랙들 (현재 재생 중인 트랙이 있는 경우)
                if (currentTrackIndex > 0) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "이전 트랙",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    itemsIndexed(
                        items = queue.subList(0, currentTrackIndex)
                    ) { index, track ->
                        QueueItem(
                            track = track,
                            isCurrentTrack = false,
                            index = index,
                            onTrackClick = { onTrackSelect(index) }
                        )
                        
                        if (index < currentTrackIndex - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
                
                // 하단 여백
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * 재생목록 항목 컴포넌트
 * @param track 트랙 정보
 * @param isCurrentTrack 현재 재생 중인 트랙인지 여부
 * @param index 트랙 인덱스
 * @param onTrackClick 트랙 클릭 이벤트
 */
@Composable
fun QueueItem(
    track: Track,
    isCurrentTrack: Boolean,
    index: Int,
    onTrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTrackClick)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentTrack) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 앨범 아트
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_playing),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 트랙 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.getTitle(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrentTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.getArtist(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    Text(
                        text = formatDuration(track.getDuration()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
            
            if (isCurrentTrack) {
                Spacer(modifier = Modifier.width(8.dp))
                
                // 현재 재생 중 표시
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}