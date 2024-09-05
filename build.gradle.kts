buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle) // Android Gradle plugin
        classpath(libs.kotlin.gradle.plugin) // Kotlin Gradle plugin
        classpath(libs.python.gradle) // Chaquopy Gradle plugin
        classpath(libs.androidx.compiler) //Android Compiler plugin
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
