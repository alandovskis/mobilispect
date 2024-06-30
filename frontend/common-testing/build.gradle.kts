plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(17)

    androidTarget {
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common-testing"
        }
    }

    sourceSets {
        val commonMain by getting
    }
}

android {
    namespace = "com.mobilispect.common.testing"
    compileSdk = libs.versions.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSDK.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}