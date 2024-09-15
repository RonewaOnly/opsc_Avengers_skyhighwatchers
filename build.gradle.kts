// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin) // Ensure this is for Kotlin 2.0 or higher
        classpath(libs.gradle) // Use the latest version of the Android Gradle Plugin
        classpath(libs.androidx.compiler) // Add this line for Compose Compiler Plugin
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Ensure to add the Compose Compiler plugin only in the app/build.gradle.kts
    // id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    kotlin("kapt") version "2.0.20" // Ensure this is compatible with Kotlin 2.0
}

// Ensure you're using compatible versions of Kotlin, Gradle, and Compose plugins
