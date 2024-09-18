buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle) // Android Gradle plugin
        classpath(libs.kotlin.gradle.plugin) // Kotlin Gradle plugin
        classpath(libs.python.gradle) // Chaquopy Gradle plugin
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.chaquo.python") version "15.0.1" apply false
}



