package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R

/**
 * 재생 컨트롤 컴포넌트
 * @param isPlaying 재생 중인지 여부
 * @param onPrevious 이전 버튼 클릭 이벤트
 * @param onNext 다음 버튼 클릭 이벤트
 * @param onPlayPause 재생/일시정지 버튼 클릭 이벤트
 * @param onShuffle 셔플 버튼 클릭 이벤트
 * @param onRepeat 반복 버튼 클릭 이벤트
 * @param isShuffleOn 셔플 활성화 여부
 * @param repeatMode 반복 모드 (0: 없음, 1: 전체 반복, 2: 한 곡 반복)
 */
@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPlayPause: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    isShuffleOn: Boolean,
    repeatMode: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 셔플 버튼
        IconButton(
            onClick = onShuffle,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = if (isShuffleOn) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                },
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // 이전 트랙 버튼
        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_previous),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 재생/일시정지 버튼
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(
                        start = if (!isPlaying) 4.dp else 0.dp,
                        end = if (isPlaying) 0.dp else 0.dp
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 다음 트랙 버튼
        IconButton(
            onClick = onNext,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // 반복 버튼
        IconButton(
            onClick = onRepeat,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = when (repeatMode) {
                    0 -> "No Repeat"
                    1 -> "Repeat All"
                    else -> "Repeat One"
                },
                tint = when (repeatMode) {
                    0 -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}