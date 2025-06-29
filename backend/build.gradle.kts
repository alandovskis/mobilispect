import com.google.protobuf.gradle.id

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.graalvm.buildtools.native") version "0.10.6"
	id("org.cyclonedx.bom") version "1.10.0"
//	id("org.springframework.cloud.contract") version "4.2.1"
	id("com.google.protobuf") version "0.9.4"
	id("com.squareup.sort-dependencies") version "0.14"
	id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

group = "com.mobilispect"
version = "0.0.10-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"
extra["springGrpcVersion"] = "0.8.0"
extra["springModulithVersion"] = "1.3.6"

dependencies {
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.grpc:grpc-services")
	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-bus")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
//	implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
	//implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
	implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
	//implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.modulith:spring-modulith-events-api")
	implementation("org.springframework.modulith:spring-modulith-starter-core")
	implementation(libs.kotlinx.serialization.csv)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.protobuf.kotlin)
//	implementation(libs.springdoc.openapi.ui)

//	runtimeOnly("io.micrometer:micrometer-registry-influx")
//	runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
	//runtimeOnly("org.springframework.modulith:spring-modulith-events-kafka")
//	runtimeOnly("org.springframework.modulith:spring-modulith-observability")

	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.rest-assured:spring-web-test-client")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.batch:spring-batch-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.grpc:spring-grpc-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
//	testImplementation("org.springframework.modulith:spring-modulith-starter-test")
//	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	//testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("org.testcontainers:vault")
//	testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

/*contracts {
	testMode = TestMode.WEBTESTCLIENT
}*/

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				id("grpc") {
					option("jakarta_omit")
					option("@generated=omit")
				}
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

/*tasks.contractTest {
	useJUnitPlatform()
}*/

// Linting
detekt {
	// Specify the base path for file paths in the formatted reports.
	// If not set, all file paths reported will be absolute file path.
	basePath = projectDir.parent
	config.setFrom(file("config/detekt/detekt.yml"))
}
