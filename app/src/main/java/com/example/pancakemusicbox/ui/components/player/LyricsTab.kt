package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pancakemusicbox.model.Track

/**
 * 가사 탭 컴포넌트
 * @param track 현재 재생 중인 트랙
 * @param currentPositionMs 현재 재생 위치 (밀리초)
 * @param lyrics 가사 데이터 (List<Pair<Long, String>>) - 타임스탬프(밀리초)와 가사 텍스트 쌍
 * @param onGenerateLyricsClick AI 가사 생성 클릭 이벤트
 */
@Composable
fun LyricsTab(
    track: Track,
    currentPositionMs: Long,
    lyrics: List<Pair<Long, String>>?,
    onGenerateLyricsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (lyrics == null || lyrics.isEmpty()) {
            // 가사가 없는 경우 AI 가사 생성 버튼 표시
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "가사를 찾을 수 없습니다",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = onGenerateLyricsClick) {
                    Text(text = "AI 가사 생성")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Powered by Whisper AI",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            // 현재 위치에 맞는 가사 라인 인덱스 찾기
            val currentLyricIndex by remember(currentPositionMs, lyrics) {
                derivedStateOf {
                    val index = lyrics.indexOfLast { (timestamp, _) -> 
                        timestamp <= currentPositionMs 
                    }
                    if (index == -1) 0 else index
                }
            }
            
            // 스크롤 상태
            val scrollState = rememberScrollState()
            
            // 현재 가사 라인으로 자동 스크롤
            LaunchedEffect(currentLyricIndex) {
                // 가사 높이를 대략적으로 계산 (50dp로 가정)
                val lineHeight = 50
                val targetScroll = currentLyricIndex * lineHeight
                scrollState.animateScrollTo(targetScroll)
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 세로 가사 스크롤
                Spacer(modifier = Modifier.height(120.dp)) // 상단 여백
                
                lyrics.forEachIndexed { index, (timestamp, text) ->
                    val isCurrent = index == currentLyricIndex
                    
                    // 가사 라인
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = if (isCurrent) 20.sp else 16.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = if (isCurrent) 1f else 0.5f
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(120.dp)) // 하단 여백
            }
        }
    }
}