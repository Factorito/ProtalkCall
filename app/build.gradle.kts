plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // 여기서 플러그인 적용
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8" // Kotlin도 동일한 버전 사용
    }
    packaging {
        resources {
            resources.excludes.add("META-INF/NOTICE.md")
            resources.excludes.add("META-INF/LICENSE.md")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
        implementation("androidx.room:room-runtime:2.5.2")
        kapt("androidx.room:room-compiler:2.5.2")
        implementation("androidx.room:room-ktx:2.5.2")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.compose.material3:material3:1.2.0")
        implementation("com.google.android.material:material:1.10.0")
        implementation("com.sun.mail:android-mail:1.6.7")
        implementation("com.sun.mail:android-activation:1.6.7")

    }
}