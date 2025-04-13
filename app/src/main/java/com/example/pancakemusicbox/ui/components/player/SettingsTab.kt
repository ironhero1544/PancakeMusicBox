package com.example.pancakemusicbox.ui.components.player

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Track

/**
 * 설정 탭 컴포넌트
 * @param track 현재 재생 중인 트랙
 * @param onEqClick EQ 버튼 클릭 이벤트
 * @param onSettingChange 설정 변경 이벤트 (key, value)
 */
@Composable
fun SettingsTab(
    track: Track,
    onEqClick: () -> Unit,
    onSettingChange: (String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    // 설정 상태들
    var bitDepth by remember { mutableIntStateOf(track.getAudioQuality().getBitDepth()) }
    var sampleRate by remember { mutableIntStateOf(track.getAudioQuality().getSampleRate()) }
    var volumeNormalization by remember { mutableStateOf(false) }
    var targetLufs by remember { mutableIntStateOf(-14) } // 기본값: -14 LUFS
    var autoHardwareSwitching by remember { mutableStateOf(true) }
    var bitDepthExpanded by remember { mutableStateOf(false) }
    var sampleRateExpanded by remember { mutableStateOf(false) }
    
    val bitDepthOptions = listOf(16, 24, 32)
    val sampleRateOptions = listOf(44100, 48000, 88200, 96000, 176400, 192000)
    val lufsRange = -23f..-9f
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 현재 오디오 정보
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "현재 오디오 정보",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "형식:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = track.getAudioQuality().getFormat(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "오디오 품질:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = track.getAudioQuality().getAudioQualityString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (track.isHighRes) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Hi-Res",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // EQ 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onEqClick)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_equalizer),
                contentDescription = "Equalizer",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "이퀄라이저 (EQ)",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Open",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        
        Divider()
        
        // 오디오 출력 설정
        Text(
            text = "오디오 출력 설정",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // 비트 뎁스 설정
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "비트 뎁스",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box {
                Text(
                    text = "${bitDepth}bit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { bitDepthExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                
                DropdownMenu(
                    expanded = bitDepthExpanded,
                    onDismissRequest = { bitDepthExpanded = false }
                ) {
                    bitDepthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("${option}bit") },
                            onClick = {
                                bitDepth = option
                                bitDepthExpanded = false
                                onSettingChange("bitDepth", option)
                            }
                        )
                    }
                }
            }
        }
        
        // 샘플링 레이트 설정
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "샘플링 레이트",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box {
                Text(
                    text = "${sampleRate / 1000}kHz",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { sampleRateExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                
                DropdownMenu(
                    expanded = sampleRateExpanded,
                    onDismissRequest = { sampleRateExpanded = false }
                ) {
                    sampleRateOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("${option / 1000}kHz") },
                            onClick = {
                                sampleRate = option
                                sampleRateExpanded = false
                                onSettingChange("sampleRate", option)
                            }
                        )
                    }
                }
            }
        }
        
        Divider()
        
        // 볼륨 관리 설정
        Text(
            text = "볼륨 관리",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // 볼륨 정규화
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "볼륨 정규화 (LUFS)",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "트랙 간 볼륨 차이를 줄여줍니다",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            
            Switch(
                checked = volumeNormalization,
                onCheckedChange = { 
                    volumeNormalization = it
                    onSettingChange("volumeNormalization", it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        
        // 타겟 LUFS 슬라이더 (볼륨 정규화가 켜진 경우만 표시)
        if (volumeNormalization) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "-23 LUFS",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    
                    Text(
                        text = "-9 LUFS",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                Slider(
                    value = targetLufs.toFloat(),
                    onValueChange = { value ->
                        targetLufs = value.toInt()
                    },
                    onValueChangeFinished = {
                        onSettingChange("targetLufs", targetLufs)
                    },
                    valueRange = lufsRange,
                    steps = 13, // 1 LUFS 단위로 조절
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Text(
                    text = "타겟: $targetLufs LUFS",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        
        Divider()
        
        // 하드웨어 관리
        Text(
            text = "하드웨어 관리",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // 자동 하드웨어 전환
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "하드웨어별 자동 설정 전환",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "연결된 오디오 장치에 맞게 설정을 자동으로 변경합니다",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            
            Switch(
                checked = autoHardwareSwitching,
                onCheckedChange = { 
                    autoHardwareSwitching = it
                    onSettingChange("autoHardwareSwitching", it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}