package com.example.pancakemusicbox

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pancakemusicbox.ui.HiFiPlayerApp
import com.example.pancakemusicbox.ui.theme.HiFiPlayerTheme
import com.example.pancakemusicbox.viewmodel.MusicViewModel
import java.io.File

class MainActivity : ComponentActivity() {
    // 필요한 권한 목록
    private val requiredPermissions: Array<String>
        get() {
            val permissions = mutableListOf<String>()
            
            // 저장소 관련 권한
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES) // 앨범 아트를 위해
            } else {
                // Android 12 이하
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    // Android 10 이하에서는 WRITE_EXTERNAL_STORAGE 권한도 필요
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            
            // 백그라운드 실행 관련 권한
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 이상에서 사용 가능한 경우
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_COMPANION_DEVICE_SETUP)) {
                    permissions.add(Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND)
                    permissions.add(Manifest.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 9-10에서는 FOREGROUND_SERVICE 권한
                permissions.add(Manifest.permission.FOREGROUND_SERVICE)
            }
            
            // 오디오 관련 권한은 모든 버전에서 필요
            permissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
            
            return permissions.toTypedArray()
        }

    // 음악 뷰모델
    private lateinit var musicViewModel: MusicViewModel

    // 권한 요청 런처
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 요청 런처 초기화
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val essentialPermissionsGranted = permissions.entries.all { entry ->
                // READ_EXTERNAL_STORAGE 또는 READ_MEDIA_AUDIO가 필수 권한
                if ((entry.key == Manifest.permission.READ_EXTERNAL_STORAGE || 
                     entry.key == Manifest.permission.READ_MEDIA_AUDIO) && !entry.value) {
                    return@all false
                }
                // 다른 권한은 없어도 앱 실행에 영향 없음
                true
            }
            
            if (essentialPermissionsGranted) {
                // 안드로이드 10 이하에서 레거시 저장소 접근 설정
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    setupLegacyStorageAccess()
                }
                
                // 약간의 지연 후 초기화 - 네이티브 라이브러리 로딩에 시간이 필요할 수 있음
                Handler(Looper.getMainLooper()).postDelayed({
                    initializeApp()
                }, 300) // 300ms 대기
            } else {
                Toast.makeText(this, "저장소 접근 권한이 필요합니다. 앱을 다시 실행해 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
            }
        }

        // 뷰모델 초기화
        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        // 권한 체크 및 요청
        checkAndRequestPermissions()

        // UI 설정
        setContent {
            HiFiPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 통합된 PlayerUI로 앱 구성 (ViewModel은 Compose 내부에서 관리)
                    HiFiPlayerApp()
                }
            }
        }
    }

    // 권한 체크 및 요청
    private fun checkAndRequestPermissions() {
        // 필요한 권한 목록 가져오기
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            Log.d("MainActivity", "Requesting permissions: ${permissionsToRequest.joinToString()}")
            
            // 안드로이드 10 이하에서 저장소 권한 거부 가능성 고려
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
                permissionsToRequest.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 이전에 거부한 적이 있는지 확인
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, 
                        "음악 파일을 스캔하려면 저장소 접근 권한이 필요합니다.",
                        Toast.LENGTH_LONG).show()
                }
            }
            
            // 권한 요청
            permissionLauncher.launch(permissionsToRequest)
        } else {
            Log.d("MainActivity", "All permissions already granted")
            initializeApp()
        }
    }
    
    // 안드로이드 10 이하에서 레거시 저장소 접근 처리
    private fun setupLegacyStorageAccess() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            try {
                // 안드로이드 10 이하에서 저장소 접근을 위한 추가 설정
                val musicDirectories = arrayOf(
                    "/storage/emulated/0/Music",
                    "/storage/emulated/0/Download",
                    "/sdcard/Music"
                )
                
                // 기존 디렉토리 접근 확인
                for (dir in musicDirectories) {
                    val directory = File(dir)
                    if (directory.exists() && !directory.canRead()) {
                        Log.w("MainActivity", "Cannot read directory: $dir")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error setting up legacy storage access", e)
            }
        }
    }

    // 앱 초기화 (권한 획득 후 호출)
    private fun initializeApp() {
        try {
            // 접근 권한이 필요한 모든 하드웨어 설정 초기화
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.isSpeakerphoneOn = false
            
            // 오디오 스캐너를 백그라운드에서 초기화
            Thread {
                try {
                    // 음악 스캔 시작 전 오디오 포커스 해제
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    
                    val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(audioAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener { }
                        .build()
                    
                    audioManager.abandonAudioFocusRequest(focusRequest)
                    
                    // 기본 음악 디렉토리 스캔
                    musicViewModel.scanDefaultMusicDirs()
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, "음악 스캔 중 오류가 발생했습니다", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "앱 초기화 중 오류가 발생했습니다", Toast.LENGTH_LONG).show()
        }
    }

    // JNI 메서드 선언 (C++에서 호출할 수 있음)
    external fun stringFromJNI(): String
}
