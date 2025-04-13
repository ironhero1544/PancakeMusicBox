package com.example.pancakemusicbox.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.navigation.Screen
import com.example.pancakemusicbox.navigation.bottomNavItems
import com.example.pancakemusicbox.ui.theme.BottomNavBackground

/**
 * 앱 하단의 네비게이션 바 컴포넌트
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(
        modifier = modifier.height(56.dp),
        containerColor = BottomNavBackground,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = when (screen) {
                            is Screen.Home -> painterResource(id = R.drawable.ic_home)
                            is Screen.Library -> painterResource(id = R.drawable.ic_library)
                            is Screen.Settings -> painterResource(id = R.drawable.ic_settings)
                            else -> painterResource(id = R.drawable.ic_home)
                        },
                        contentDescription = getScreenLabel(screen)
                    )
                },
                label = { Text(text = getScreenLabel(screen)) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // 백 스택 설정으로 탭 전환 최적화
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

/**
 * 화면별 라벨 텍스트 반환
 */
@Composable
private fun getScreenLabel(screen: Screen): String {
    return when (screen) {
        is Screen.Home -> "홈"
        is Screen.Library -> "라이브러리"
        is Screen.Settings -> "설정"
        else -> ""
    }
}
