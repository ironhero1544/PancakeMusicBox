package com.example.pancakemusicbox.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pancakemusicbox.R
import com.example.pancakemusicbox.model.Album
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.ui.theme.HighResAudio
import java.util.UUID

/**
 * 트랙 목록에서 앨범 목록 추출하는 유틸리티 함수
 */
fun extractAlbumsFromTracks(tracks: List<Track>): List<Album> {
    val albumMap = mutableMapOf<String, MutableList<Track>>()
    
    // 트랙을 앨범별로 그룹화
    tracks.forEach { track ->
        val albumTitle = track.getAlbumTitle()
        if (!albumMap.containsKey(albumTitle)) {
            albumMap[albumTitle] = mutableListOf()
        }
        albumMap[albumTitle]?.add(track)
    }
    
    // Album 객체 생성
    return albumMap.map { (albumTitle, albumTracks) ->
        // 대표 트랙 (첫 번째)
        val representativeTrack = albumTracks.first()
        
        Album(
            id = UUID.randomUUID().toString(), // 고유 ID 생성
            title = albumTitle,
            artist = representativeTrack.getArtist(),
            artworkUri = representativeTrack.getAlbumArtUri(),
            year = representativeTrack.getYear(),
            tracks = albumTracks,
            genre = representativeTrack.getGenre()
        )
    }.sortedWith(compareByDescending { it.getYear() })
}

/**
 * 앨범 그리드 아이템 컴포넌트
 */
@Composable
fun AlbumGridItem(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // 앨범 커버
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 Coil로 실제 이미지 로드
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // 하이레스 오디오 인디케이터 (앨범의 모든 트랙이 하이레스인 경우)
            if (album.getTracks().all { it.isHighRes }) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(HighResAudio)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 앨범 제목
        Text(
            text = album.getTitle(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // 아티스트 이름 & 연도
        Text(
            text = album.getArtist() + (album.getYear()?.let { " • $it" } ?: ""),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 앨범 탭 콘텐츠
 */
@Composable
fun AlbumsTab(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(albums) { album ->
            AlbumGridItem(
                album = album,
                onClick = { onAlbumClick(album) }
            )
        }
    }
}
