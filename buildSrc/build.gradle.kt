import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("org.springframework.boot") version Versions.spring_bootsadf
    id("io.spring.dependency-management") version Versions.spring_dependency_management
    kotlin("jvm") version Versions.jvm_plugin
    kotlin("plugin.spring") version Versions.jvm_plugin
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:${Versions.spring_boot}")
    implementation("org.springframework.boot:spring-boot-starter-webflux:${Versions.spring_boot}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:${Versions.kotlin_extensions}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.coroutines_reactor}")

    compileOnly("org.projectlombok:lombok:${Versions.lombok}")

    runtimeOnly("io.r2dbc:r2dbc-postgresql:${Versions.r2dbc}")
    runtimeOnly("org.postgresql:postgresql:${Versions.postgresql}")

    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.spring_boot}")
    testImplementation("io.projectreactor:reactor-test:${Versions.reactor_test}")
}

group = "org.eljabali.sami.todo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
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

tasks.named<BootRun>("bootRun") {
    args("--spring.profiles.active=dev")
}