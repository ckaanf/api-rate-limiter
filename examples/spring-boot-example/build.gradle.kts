plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

group = "io.github.ckaanf"
version = "1.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation(project(":integrations:spring-boot-starter"))
    implementation(project(":core"))
    implementation(project(":algorithms:token-bucket"))
    implementation(project(":storage:inmemory"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
