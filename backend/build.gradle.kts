import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: Remove once https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed - for alias call
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(libs.ktor.core)
    implementation(libs.ktor.contentNegociation)
    implementation(libs.ktor.resources)
    implementation(libs.ktor.serialization.json)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:mongodb:1.17.6")
    testImplementation(libs.ktor.testing)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Mutation Testing
pitest {
    setProperty("junit5PluginVersion", "1.0.0")
    setProperty("threads", 2)
    setProperty("outputFormats", listOf("HTML"))
    setProperty("avoidCallsTo", listOf("kotlin.jvm.internal"))
}