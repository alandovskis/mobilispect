// TODO: Remove once https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed - for alias call
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.detekt)
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

android {
    compileSdk = libs.versions.compileSDK.get().toInt()
    defaultConfig {
        applicationId = "com.mobilispect.android"
        minSdk = libs.versions.minSDK.get().toInt()
        targetSdk = libs.versions.targetSDK.get().toInt()
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Use HiltTestApplication in instrumented tests
        testInstrumentationRunner = "com.mobilispect.android.CustomTestRunner"
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
        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
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
        kotlinCompilerExtensionVersion = "1.4.2"
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
    namespace = "com.mobilispect.android"

    detekt {
        // Specify the base path for file paths in the formatted reports.
        // If not set, all file paths reported will be absolute file path.
        basePath = projectDir.parent
        toolVersion = "1.22.0"
        config = files("../config/detekt/detekt.yml")
    }
}

dependencies {
    implementation(project(":common"))
    testImplementation(project(":common-testing"))
    androidTestImplementation(project(":common-testing"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //region Dependency Injection
    implementation(libs.hilt.android)
    androidTestImplementation(libs.androidx.test.runner)
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

    implementation(libs.room.ktx)
    androidTestImplementation(libs.room.testing)

    implementation(libs.navigation.compose)
    androidTestImplementation(libs.navigation.testing)

    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.testing)

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}