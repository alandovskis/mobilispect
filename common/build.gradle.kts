@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    kotlin("kapt")
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.library")
    kotlin("plugin.serialization") version "1.6.10"
    id("dagger.hilt.android.plugin")
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        val commonMain by getting {
            dependencies {
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
                implementation(kotlin("test"))
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
                kapt {
                    arguments {
                        arg("room.schemaLocation", "$projectDir/schemas")
                    }
                }
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.room:room-testing:2.4.2")
            }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 25
        targetSdk = 32
    }
}