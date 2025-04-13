package com.example.pancakemusicbox.ui.components.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pancakemusicbox.model.Track
import com.example.pancakemusicbox.viewmodel.AudioPlayerViewModel

/**
 * 플레이어 탭 콘텐츠 컴포넌트
 * 탭 인덱스에 따라 적절한 탭 컴포넌트를 표시
 *
 * @param tabIndex 현재 선택된 탭 인덱스 (0: 가사, 1: 설정, 2: 재생목록)
 * @param track 현재 재생 중인 트랙
 * @param currentPositionMs 현재 재생 위치 (밀리초)
 * @param audioViewModel 오디오 플레이어 뷰모델
 * @param lyrics 가사 데이터
 */
@Composable
fun PlayerTabContent(
    tabIndex: Int,
    track: Track,
    currentPositionMs: Long,
    audioViewModel: AudioPlayerViewModel,
    lyrics: List<Pair<Long, String>>?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (tabIndex) {
            0 -> {
                // 가사 탭
                LyricsTab(
                    track = track,
                    currentPositionMs = currentPositionMs,
                    lyrics = lyrics,
                    onGenerateLyricsClick = {
                        // AI 가사 생성 기능 호출
                        // 실제 구현에서는 여기에 Whisper AI 호출 로직이 들어갈 수 있음
                    }
                )
            }
            1 -> {
                // 설정 탭
                SettingsTab(
                    track = track,
                    onEqClick = {
                        // EQ 화면으로 이동 또는 EQ 다이얼로그 표시
                    },
                    onSettingChange = { key, value ->
                        // 설정 변경 처리
                        when (key) {
                            "bitDepth" -> audioViewModel.setAudioQuality(
                                track.getAudioQuality().getSampleRate(),
                                value as Int,
                                track.getAudioQuality().getChannels()
                            )
                            "sampleRate" -> audioViewModel.setAudioQuality(
                                value as Int,
                                track.getAudioQuality().getBitDepth(),
                                track.getAudioQuality().getChannels()
                            )
                            "volumeNormalization" -> audioViewModel.toggleVolumeNormalization()
                            "targetLufs" -> audioViewModel.setTargetLufs(value as Int)
                            // 기타 설정 처리
                        }
                    }
                )
            }
            2 -> {
                // 재생목록 탭
                val queue = audioViewModel.playlist.value ?: emptyList()
                val currentTrackIndex = audioViewModel.currentTrackIndex.value ?: 0
                
                PlaylistTab(
                    currentTrack = track,
                    queue = queue,
                    currentTrackIndex = currentTrackIndex,
                    onTrackSelect = { index ->
                        // 선택한 트랙 재생
                        audioViewModel.loadTrack(index)
                    },
                    onMoveTrack = { fromIndex, toIndex ->
                        // 트랙 순서 변경
                        audioViewModel.moveTrack(fromIndex, toIndex)
                    },
                    onSaveQueue = {
                        // 현재 재생목록 저장
                        audioViewModel.saveCurrentPlaylistAs("새 플레이리스트")
                    },
                    onClearQueue = {
                        // 재생목록 비우기 (현재 트랙 유지)
                        val currentTrack = queue.getOrNull(currentTrackIndex)
                        if (currentTrack != null) {
                            audioViewModel.setPlaylist(listOf(currentTrack))
                        }
                    },
                    onShuffleQueue = {
                        // 재생목록 셔플
                        audioViewModel.toggleShuffle()
                    }
                )
            }
        }
    }
}