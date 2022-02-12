import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "org.eljabali.sami.todo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:2.6.3")
	implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.0-native-mt")

	compileOnly("org.projectlombok:lombok:1.18.22")

	runtimeOnly("org.springframework.boot:spring-boot-devtools:2.6.3")
//	runtimeOnly("io.r2dbc:r2dbc-postgresql")
//	runtimeOnly("org.postgresql:postgresql")

	annotationProcessor("org.projectlombok:lombok:1.18.22")

	testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.3")
	testImplementation("io.projectreactor:reactor-test:3.4.14")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
