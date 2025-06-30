plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("ApiRateLimitingExample")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":algorithms:token-bucket"))
    implementation(project(":storage:inmemory"))

    // HTTP 클라이언트 시뮬레이션용
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON 처리
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")

    // 로깅
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // 유틸리티
    implementation("org.apache.commons:commons-lang3:3.14.0")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}