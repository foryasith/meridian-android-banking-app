
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")

    // Firebase Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.meridian.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.meridian.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // Existing XML screens
        dataBinding = true

        // Jetpack Compose
        compose = true
    }

    // Compatible with Kotlin 1.9.24
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        )
    }
}

dependencies {

    // ---------------------------------------------------------
    // Core Android
    // ---------------------------------------------------------

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")

    // ---------------------------------------------------------
    // Room - Keep until Firestore migration is complete
    // ---------------------------------------------------------

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // ---------------------------------------------------------
    // Lifecycle / MVVM
    // ---------------------------------------------------------

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ---------------------------------------------------------
    // RecyclerView
    // ---------------------------------------------------------

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ---------------------------------------------------------
    // Session Reminder Service
    // ---------------------------------------------------------

    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

    // ---------------------------------------------------------
    // Biometric Authentication
    // ---------------------------------------------------------

    implementation("androidx.biometric:biometric:1.1.0")

    // ---------------------------------------------------------
    // Jetpack Compose
    // ---------------------------------------------------------

    val composeBom = platform(
        "androidx.compose:compose-bom:2024.05.00"
    )

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ---------------------------------------------------------
    // Lab 10 - Firebase
    // ---------------------------------------------------------

    val firebaseBom = platform(
        "com.google.firebase:firebase-bom:33.7.0"
    )

    implementation(firebaseBom)

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")

    // ---------------------------------------------------------
    // Local Unit Testing
    // ---------------------------------------------------------

    testImplementation("junit:junit:4.13.2")

    // ---------------------------------------------------------
    // Android Instrumented Testing
    // ---------------------------------------------------------

    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )
}
