plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(17)

    androidTarget {}

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
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.core)
                implementation(libs.ktor.contentNegociation)
                implementation(libs.ktor.resources)
                implementation(libs.ktor.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":common-testing"))
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.testing)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.javax.inject)
                implementation(libs.dagger)
                implementation(libs.kotlinx.serialization)
                implementation(libs.hilt.android)
                api(libs.bundles.retrofit)
                implementation(libs.room.ktx)
                api(libs.appcompat)
                api(libs.core.ktx)
                implementation(libs.okhttp.profiler)
                implementation(libs.ktor.okhttp)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.room.testing)
                implementation(libs.truth)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockWebServer)
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.hilt.android.testing)
            }
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
    compileSdk = libs.versions.compileSDK.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSDK.get().toInt()

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    compileOptions {
        // Enable de-sugaring of Java 8 APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "com.mobilispect.common"

    dependencies {
        coreLibraryDesugaring(libs.android.desugar.libs)
    }

    buildFeatures {
        // Generate BuildConfig
        buildConfig = true
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspAndroid", libs.hilt.compiler)
}