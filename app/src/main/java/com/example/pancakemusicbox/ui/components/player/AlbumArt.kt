package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track

/**
 * 앨범 아트 컴포넌트
 * @param track 트랙 정보
 * @param modifier 모디파이어
 */
@Composable
fun AlbumArt(
    track: Track,
    modifier: Modifier = Modifier
) {
    // 그라데이션 효과를 위한 색상
    val gradientColors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        // 앨범 아트 (실제로는 Coil로 이미지 로드)
        // 이 샘플에서는 placeholder 아이콘 사용
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // 오디오 스펙트럼을 위한 그라데이션 오버레이 (하단)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxSize(0.3f) // 하단 30%만 차지
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            // 실제 구현에서는 여기에 스펙트럼 시각화가 들어갈 수 있음
        }
    }
}