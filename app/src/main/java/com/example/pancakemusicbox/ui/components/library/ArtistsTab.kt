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
import com.example.pancakemusicbox.model.Track
import java.util.UUID

/**
 * 아티스트 모델 클래스 (나중에 model 패키지로 이동할 수 있음)
 */
class Artist {
    private val _id: String
    private val _name: String
    private val _imageUrl: String?
    private val _trackCount: Int
    private val _albumCount: Int

    constructor(
        id: String = UUID.randomUUID().toString(),
        name: String,
        imageUrl: String? = null,
        trackCount: Int = 0,
        albumCount: Int = 0
    ) {
        this._id = id
        this._name = name
        this._imageUrl = imageUrl
        this._trackCount = trackCount
        this._albumCount = albumCount
    }

    // Getter 메서드
    fun getId(): String = _id
    fun getName(): String = _name
    fun getImageUrl(): String? = _imageUrl
    fun getTrackCount(): Int = _trackCount
    fun getAlbumCount(): Int = _albumCount
}

/**
 * 트랙 목록에서 아티스트 목록 추출하는 유틸리티 함수
 */
fun extractArtistsFromTracks(tracks: List<Track>): List<Artist> {
    val artistMap = mutableMapOf<String, MutableList<Track>>()
    
    // 트랙을 아티스트별로 그룹화
    tracks.forEach { track ->
        val artistName = track.getArtist()
        if (!artistMap.containsKey(artistName)) {
            artistMap[artistName] = mutableListOf()
        }
        artistMap[artistName]?.add(track)
    }
    
    // 아티스트별 앨범 카운트 계산 및 Artist 객체 생성
    return artistMap.map { (artistName, artistTracks) ->
        val albumSet = artistTracks.map { it.getAlbumTitle() }.toSet()
        
        Artist(
            id = UUID.randomUUID().toString(), // 고유 ID 생성
            name = artistName,
            // 이미지 URL은 첫 번째 트랙의 앨범 아트로 대체
            imageUrl = artistTracks.firstOrNull()?.getAlbumArtUri(),
            trackCount = artistTracks.size,
            albumCount = albumSet.size
        )
    }.sortedBy { it.getName() } // 이름순 정렬
}

/**
 * 아티스트 그리드 아이템 컴포넌트
 */
@Composable
fun ArtistGridItem(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아티스트 이미지 (원형)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 Coil로 실제 이미지 로드
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 아티스트 이름
        Text(
            text = artist.getName(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // 트랙/앨범 수
        Text(
            text = "${artist.getTrackCount()}곡 • ${artist.getAlbumCount()}앨범",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 아티스트 탭 콘텐츠
 */
@Composable
fun ArtistsTab(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(artists) { artist ->
            ArtistGridItem(
                artist = artist,
                onClick = { onArtistClick(artist) }
            )
        }
    }
}
