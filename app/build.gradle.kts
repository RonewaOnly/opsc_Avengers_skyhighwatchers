import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.gms.google-services")
    id ("kotlin-kapt")  // <-- Add this line

}

// Loading the Mapbox API
val apikeyPref = rootProject.file("APIKEYS.properties")
val apikeyproperties = Properties().apply {
    load(FileInputStream(apikeyPref))
}

android {
    namespace = "com.example.skyhigh_prototype"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.skyhigh_prototype"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Load the API key from local.properties
        val apiKey = project.findProperty("GOOGLE_API_KEY")?.toString() ?: ""
        val apikey2 = project.findProperty("MAPBOX_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GOOGLE_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "MAPBOX_API_KEY", "\"$apikey2\"")

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
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Ensure this version is compatible with Compose version you are using
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Import the Firebase BoM
    implementation (platform("com.google.firebase:firebase-bom:32.2.0")) // Adjust to latest version
    // Add the dependency for Firebase Storage
    implementation ("com.google.firebase:firebase-storage")
    // Map Box
    implementation("com.mapbox.maps:android:11.6.1")
    implementation("com.mapbox.extension:maps-compose:11.6.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:2.2.0")

    // Splash
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.location)

    // Camera
    val cameraVersion = "1.2.2"
    implementation("androidx.camera:camera-core:$cameraVersion")
    implementation("androidx.camera:camera-camera2:$cameraVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraVersion")
    implementation("androidx.camera:camera-video:$cameraVersion")
    implementation("androidx.camera:camera-view:$cameraVersion")
    implementation("androidx.camera:camera-extensions:$cameraVersion")

    // Video
    implementation("com.google.accompanist:accompanist-permissions:0.31.0-alpha")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.8.8")

    // Processing images
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")

    // Room
    val roomVersion = "2.6.1" // Latest stable version as of now
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    implementation("androidx.compose.material:material:1.7.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}