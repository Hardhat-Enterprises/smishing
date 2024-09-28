// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://chaquo.com/maven") }
        // Add other repositories here if needed
    }
    dependencies {
        classpath(libs.gradle)
        classpath("com.chaquo.python:gradle:15.0.1")
        // Add other classpaths here if needed
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.chaquo.python") version "15.0.1" apply false
}

