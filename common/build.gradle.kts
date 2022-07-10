@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.compose.compose
import kotlin.text.Typography.dagger

plugins {
    kotlin("multiplatform")
    kotlin("kapt")
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.library")
    kotlin("plugin.serialization") version "1.6.10"
    id("dagger.hilt.android.plugin")
}

group = "com.mobilispect"
version = "1.0"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api("javax.inject:javax.inject:1")
                api("com.squareup.retrofit2:retrofit:2.9.0")
                api("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation("com.google.dagger:dagger:2.42")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation("androidx.room:room-ktx:2.4.2")
                implementation("com.google.dagger:hilt-android:2.42")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.4.2")
                api("androidx.core:core-ktx:1.8.0")
                implementation("com.google.dagger:hilt-android:2.42")
                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "androidx.room",
                        "room-compiler",
                        "2.4.2"
                    )
                )
                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "hilt-compiler",
                        "2.42"
                    )
                )
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.room:room-testing:2.4.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 32
        targetSdk = (32)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}