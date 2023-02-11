plugins {
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

koverMerged {
    enable()

    filters {
        classes {
            excludes += listOf(
                "com.mobilispect.*.BuildConfig",
                "com.mobilispect.android.ui.Hilt_MainActivity*",
                "com.mobilispect.android.ui.ComposableSingletons*",
                "com.mobilispect.*.*_Factory",
                "com.mobilispect.*.*.*.*_Factory*",
                "com.mobilispect.android.ui.*.*_Hilt*",
                "com.mobilispect.common.*Module",
                "com.mobilispect.common.*Module_*",
                "com.mobilispect.common.data.*_Impl*",
                "dagger.hilt.internal.aggregatedroot.codegen.*",
                "hilt_aggregated_deps._*"
            )
        }
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.45")
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