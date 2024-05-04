plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

android {
    namespace = "com.example.tunetracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tunetracker"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //for icons
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    // Media Player
    implementation(libs.androidx.media3.datasource.okhttp)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.legacy.support.v4) // Needed MediaSessionCompat.Token
    // Glide
    implementation(libs.glide)
    implementation(libs.accompanist.permissions)
    implementation (libs.glide.compose)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // Coil
    implementation(libs.coil.compose)
    //Palette
    implementation(libs.androidx.palette.ktx)

    // Retrofit
    implementation(libs.retrofit)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    //Gson Converter
    implementation (libs.gson)
    implementation (libs.converter.gson)
    //for locking orientation
    implementation (libs.androidx.ui.util)

}