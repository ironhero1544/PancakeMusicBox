package com.example.pancakemusicbox.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.navigation.Screen

/**
 * 홈 화면 상단 앱바 컴포넌트
 */
@Composable
fun HomeTopBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 홈 타이틀
        Text(
            text = "홈",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 검색 버튼
        IconButton(
            onClick = { navController.navigate(Screen.Search.route) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 라이브러리 화면 상단 앱바 컴포넌트
 */
@Composable
fun LibraryTopBar(
    navController: NavController,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 라이브러리 타이틀
        Text(
            text = "라이브러리",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 파일 불러오기 버튼
        IconButton(
            onClick = onImportClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach_file),
                contentDescription = "Import Files",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 검색 버튼
        IconButton(
            onClick = { navController.navigate(Screen.Search.route) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 설정 화면 상단 앱바 컴포넌트
 */
@Composable
fun SettingsTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 설정 타이틀
        Text(
            text = "설정",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
