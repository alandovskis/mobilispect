plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.detekt)
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.mobilispect.android"
        minSdk = 25
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // Enable de-sugaring of Java 8 APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    packagingOptions {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions
    }

    lint {
        // SARIF is the format supported by GitHub Pull Requests.
        sarifReport = true

        // Merge reports of dependencies together.
        checkDependencies = true
    }
}

dependencies {
    implementation(project(":common"))

    //region Dependency Injection
    implementation(libs.hilt.android)
    testImplementation(libs.junit)
    kapt(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)
    //endregion

    //region UI
    implementation(libs.bundles.compose)
    // Integration with ViewModels
    implementation(libs.lifecycle.viewmodel.compose)

    //region Architecture Components
    implementation(libs.bundles.lifecycle)
    implementation(libs.hilt.navigation.compose)
    //endregion

    // UI Tests
    androidTestImplementation(libs.compose.ui.test.junit)
    debugImplementation(libs.compose.ui.test.manifest)
    //endregion

    //region Test Assertions
    androidTestImplementation(libs.truth)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    //endregion

    coreLibraryDesugaring(libs.android.desugar.libs)
}

