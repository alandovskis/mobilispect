pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Mobilispect"
include(":android")
include(":common")
include(":benchmark")
include(":common-testing")
