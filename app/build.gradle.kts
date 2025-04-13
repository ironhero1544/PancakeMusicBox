plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.pancakemusicbox"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pancakemusicbox"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
        viewBinding = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compose.compiler.get()
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 안드로이드 코어 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    // LiveData for Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.6.3")

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)

    // 네비게이션 및 상태 관리
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // 비동기 처리 (코루틴)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // 미디어 처리
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    // 네이티브 오디오 처리
    // implementation(libs.oboe) // 주석 처리 - 직접 제공한 모의 라이브러리 사용

    // 데이터베이스 (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // 의존성 주입
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // 직렬화 및 JSON 처리
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.gson)

    // 네트워킹
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    // 이미지 로딩
    implementation(libs.coil.compose)

    // UI 컴포넌트
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // 로깅
    implementation(libs.timber)

    // 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)
}