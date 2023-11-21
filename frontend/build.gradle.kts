plugins {
    alias(libs.plugins.kover)
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}

group = "com.mobilispect"
version = "1.0"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}