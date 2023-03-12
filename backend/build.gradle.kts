// TODO: Remove once https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed - for alias call
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("info.solidsoft.pitest") version "1.9.11"
    id("org.cyclonedx.bom") version "1.7.4"
}

group = "com.mobilispect"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
}

// Mutation Testing
pitest {
    setProperty("junit5PluginVersion", "1.0.0")
    setProperty("threads", 2)
    setProperty("outputFormats", listOf("HTML"))
    setProperty("avoidCallsTo", listOf("kotlin.jvm.internal"))
}

// Linting
detekt {
    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir.parent
    toolVersion = "1.22.0"
    config = files("config/detekt/detekt.yml")
}