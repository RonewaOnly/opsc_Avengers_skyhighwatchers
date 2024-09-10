import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}
//Loading the mapbox api
val apikeyPref = rootProject.file("APIKEYS.properties")
val apikeyproperties = Properties()
apikeyproperties.load(FileInputStream(apikeyPref))
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
        val apiKey = project.hasProperty("GOOGLE_API_KEY")
        val apikey2 = project.hasProperty("MAPBOX_API_KEY")
        buildConfigField ("String", "GOOGLE_API_KEY", "\"${apiKey}\"")
        buildConfigField ("String", "MAPBOX_API_KEY", "\"${apikey2}\"")

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
        viewBinding =true
        buildConfig = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Map Box
    implementation("com.mapbox.maps:android:11.6.0")
    implementation("com.mapbox.extension:maps-compose:11.6.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:2.2.0")

    //Splash
    implementation (libs.androidx.core.splashscreen)

    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.firebase.auth.v2103)
    implementation(libs.firebase.firestore)


    //camera
    val cameraVersion = "1.2.2"
    implementation ("androidx.camera:camera-core:${cameraVersion}")
    implementation ("androidx.camera:camera-camera2:${cameraVersion}")
    implementation ("androidx.camera:camera-lifecycle:${cameraVersion}")
    implementation ("androidx.camera:camera-video:${cameraVersion}")
    implementation ("androidx.camera:camera-view:${cameraVersion}")
    implementation ("androidx.camera:camera-extensions:${cameraVersion}")
    //Video
    implementation ("com.google.accompanist:accompanist-permissions:0.31.0-alpha")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("androidx.camera:camera-video:1.1.0-alpha01")

    implementation ("androidx.camera:camera-video:1.3.2")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //User location service
    //implementation ("com.google.android.gms:play-services-location:21.0.1")

    //Auth
    

    //processing images
    implementation( "io.coil-kt:coil-compose:2.4.0")
    // Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.1")


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