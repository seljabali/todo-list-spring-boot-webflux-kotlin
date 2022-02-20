import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0-M1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"
}

group = "org.eljabali.sami.todo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["testcontainersVersion"] = "1.16.2"
val kotlinCoVersion = project.properties["kotlinCoVersion"]
val kotestVersion = project.properties["kotestVersion"]
val mockkVersion = project.properties["mockkVersion"]
val springmockkVersion = project.properties["springmockkVersion"]
val springdocVersion = project.properties["springdocVersion"]

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // spring data r2dbc and postgres drivers
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")

    //spring doc support
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-kotlin:${springdocVersion}")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:${springdocVersion}")
    implementation("org.springdoc:springdoc-openapi-security:${springdocVersion}")

    //kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // development stage support
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // test dependencies
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // use mockk as mocking framework
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")

    // test helpers for Kotlin coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlinCoVersion}")

    // Kotest assertions
    testImplementation("io.kotest:kotest-assertions-core-jvm:${kotestVersion}")

    // mockk: mocking framework for Kotlin
    testImplementation("io.mockk:mockk:${mockkVersion}")

    // mockk spring integration
    testImplementation("com.ninja-squad:springmockk:${springmockkVersion}")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    buildInfo()
}