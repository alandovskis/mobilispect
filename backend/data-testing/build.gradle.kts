// TODO: Remove once https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed - for alias call
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

group = "com.mobilispect"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":data"))
    implementation("org.testcontainers:junit-jupiter:1.17.6")
    implementation("org.testcontainers:mongodb:1.17.6")
    implementation("org.springframework.boot:spring-boot-starter-test")
}