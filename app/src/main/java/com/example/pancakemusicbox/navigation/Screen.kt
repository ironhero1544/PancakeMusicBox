package com.example.pancakemusicbox.navigation

/**
 * 앱 내 화면 이동을 위한 라우트 정의
 */
sealed class Screen(val route: String) {
    // 메인 탭 화면들
    object Home : Screen("home_screen")
    object Library : Screen("library_screen")
    object Settings : Screen("settings_screen")
    
    // 세부 화면들
    object Player : Screen("player_screen")
    object Playlist : Screen("playlist_screen/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_screen/$playlistId"
    }
    object AlbumDetail : Screen("album_screen/{albumId}") {
        fun createRoute(albumId: String) = "album_screen/$albumId"
    }
    
    object PlaylistDetail : Screen("playlist_detail_screen/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_detail_screen/$playlistId"
    }
    object ArtistDetail : Screen("artist_screen/{artistId}") {
        fun createRoute(artistId: String) = "artist_screen/$artistId"
    }
    object Search : Screen("search_screen")
    object Equalizer : Screen("equalizer_screen")
}

/**
 * 하단 네비게이션 바에 표시될 항목들
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Settings
)
