plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "app.televault"
    compileSdk = 35

    defaultConfig {
        applicationId = "app.televault"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0.0"
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }

        // TeleVault's own Telegram API keys (register ONE app at my.telegram.org, once).
        // Set TG_API_ID / TG_API_HASH as environment variables (e.g. GitHub Actions secrets)
        // or in ~/.gradle/gradle.properties. Users never see or type them.
        val tgId = (System.getenv("TG_API_ID") ?: (project.findProperty("TG_API_ID") as String?) ?: "0").trim()
        val tgHash = (System.getenv("TG_API_HASH") ?: (project.findProperty("TG_API_HASH") as String?) ?: "").trim()
        buildConfigField("int", "TG_API_ID", if (tgId.toIntOrNull() != null) tgId else "0")
        buildConfigField("String", "TG_API_HASH", "\"$tgHash\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.biometric:biometric:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // TDLib: lets the app log in as YOU (needed to use Saved Messages; bots can't).
    // If this artifact ever fails to resolve, see README_TDLIB.md for alternatives.
    implementation("com.github.tdlibx:td:1.8.56")
}
