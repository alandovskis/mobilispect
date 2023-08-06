import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    id("info.solidsoft.pitest") version "1.9.11"
    id("org.cyclonedx.bom") version "1.7.4"
    alias(libs.plugins.square.sortDependencies)
    alias(libs.plugins.springdoc.openapi)
}

group = "com.mobilispect"
version = "0.0.9-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(libs.kotlinx.serialization.csv)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.resilience4j.spring)
    implementation(libs.springdoc.openapi.ui)

    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
    testImplementation("org.testcontainers:mongodb:1.18.3")
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

// Linting
detekt {
    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir.parent
    toolVersion = "1.23.1"
    config = files("config/detekt/detekt.yml")
}