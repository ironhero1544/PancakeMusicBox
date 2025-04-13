package com.example.pancakemusicbox.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.navigation.Screen
import com.example.pancakemusicbox.ui.theme.HighResAudio
import com.example.pancakemusicbox.ui.theme.MiniPlayerBackground
import kotlinx.coroutines.delay

/**
 * 하단에 표시되는 미니 플레이어 컴포넌트
 */
@Composable
fun MiniPlayer(
    track: Track,
    isPlaying: Boolean = true,
    navController: NavController,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showQualityInfo by remember { mutableStateOf(false) }
    
    // 3-5초마다 음질 정보와 아티스트 이름을 번갈아 표시
    LaunchedEffect(key1 = showQualityInfo) {
        delay(if (showQualityInfo) 5000 else 3000)
        showQualityInfo = !showQualityInfo
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { navController.navigate(Screen.Player.route) },
        color = MiniPlayerBackground,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 앨범 아트
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                // 실제 앱에서는 Coil 등을 사용하여 실제 이미지 로드
                Icon(
                    painter = painterResource(id = R.drawable.ic_placeholder),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 트랙 정보
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                // 트랙 제목
                Text(
                    text = track.getTitle(),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 아티스트/작곡가 이름 또는 음질 정보 (번갈아 표시)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 하이레스 오디오 표시 아이콘
                    if (track.isHighRes) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(HighResAudio)
                                .padding(end = 4.dp)
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = !showQualityInfo,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Text(
                            text = track.getComposer() ?: track.getArtist(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = showQualityInfo,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Text(
                            text = track.getAudioQuality().getAudioQualityString(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // 재생 제어 버튼
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 재생/일시정지 버튼
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // 다음 트랙 버튼
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Next Track",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}