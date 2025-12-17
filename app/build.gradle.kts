import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
}

val openAiApiKey: String = gradleLocalProperties(rootDir, providers).getProperty("openai_api_key")

android {
    namespace = "de.luh.hci.mid.monumentgo"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.luh.hci.mid.monumentgo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // these are generally safe to expose publicly
        buildConfigField("String", "SUPABASE_URL", "\"https://sawuorsyrkahywydvvcn.supabase.co\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNhd3VvcnN5cmthaHl3eWR2dmNuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1Mzc5MDQsImV4cCI6MjA4MDExMzkwNH0._THv4gh4H_osMM4M6INP2NVIb7PkWJfrEtwju9veA7w\"")
        buildConfigField("String", "OPENAI_API_KEY", "\"${openAiApiKey}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Added by Erik (MainScreen)
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)

    // CAMERA RELATED
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)


    // SUPABASE RELATED
    implementation(platform(libs.supabase.kt.bom))
    implementation(libs.supabase.kt.postgrest)
    implementation(libs.ktor.client)

    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.vision.internal.vkp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
}