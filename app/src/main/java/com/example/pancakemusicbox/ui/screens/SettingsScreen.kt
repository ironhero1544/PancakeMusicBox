package com.example.pancakemusicbox.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// SampleData import 제거
import com.example.pancakemusicbox.ui.components.SettingsTopBar

/**
 * 설정 화면
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 앱바
            SettingsTopBar()
            
            // 설정 항목들 (스크롤 가능)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // 설정 섹션들 (임시 구현, 추후 실제 설정으로 대체)
                SettingsSection(title = "EQ 및 오디오 처리")
                SettingsSection(title = "오디오 품질 설정")
                SettingsSection(title = "볼륨 관리 (LUFS 기반)")
                SettingsSection(title = "재생 설정")
                SettingsSection(title = "하드웨어 관리")
                SettingsSection(title = "가사 설정")
                SettingsSection(title = "재생목록/대기열 설정")
                
                // 하단 여백 (미니플레이어 및 네비게이션 바 높이만큼)
                Spacer(modifier = Modifier.height(128.dp))
            }
        }
    }
}

/**
 * 설정 섹션 컴포넌트
 */
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 섹션 헤더
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        // 임시 설정 항목 (데모용)
        for (i in 1..2) {
            SettingsItem(
                title = "설정 항목 $i",
                description = "설정 항목 ${i}에 대한 설명 텍스트입니다."
            )
        }
        
        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * 개별 설정 항목 컴포넌트
 */
@Composable
fun SettingsItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(end = 60.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        
        // 스위치 (데모용)
        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}
