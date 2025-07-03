plugins {
    `java-library`
    `maven-publish`
}

allprojects {
    group = "io.github.ckaanf"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    tasks.test {
        useJUnitPlatform()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.9.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-core:5.7.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.1.1")
        testImplementation("org.assertj:assertj-core:3.24.2")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                artifactId = getArtifactId(project.path)

                pom {
                    name.set("${project.group}:${artifactId}")
                    description.set(getModuleDescription(project.path))
                    url.set("https://github.com/ckaanf/api-rate-limiter")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("ckaanf")
                            name.set("ckaanf")
                            email.set("skywlstn777@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/ckaanf/api-rate-limiter.git")
                        developerConnection.set("scm:git:ssh://github.com/ckaanf/api-rate-limiter.git")
                        url.set("https://github.com/ckaanf/api-rate-limiter")
                    }
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository> {
    enabled = false
}
tasks.withType<PublishToMavenLocal> {
    enabled = false
}

fun getArtifactId(projectPath: String): String {
    return when (projectPath) {
        ":core" -> "core"
        ":algorithms:token-bucket" -> "algorithm-token-bucket"
        ":storage:inmemory" -> "storage-inmemory"
        ":storage:redis" -> "storage-redis"
        ":integrations:spring-boot-starter" -> "spring-boot-starter"
        else -> project.name.replace(":", "-")
    }
}

fun getModuleDescription(projectPath: String): String {
    return when (projectPath) {
        ":core" -> "API Rate Limiter - Core interfaces and contracts"
        ":algorithms:token-bucket" -> "API Rate Limiter - Token Bucket algorithm implementation"
        ":storage:inmemory" -> "API Rate Limiter - In-memory storage implementation"
        ":storage:redis" -> "API Rate Limiter - Redis storage implementation"
        ":integrations:spring-boot-starter" -> "API Rate Limiter - Spring Boot Auto Configuration"
        else -> "API Rate Limiter - ${projectPath.removePrefix(":")}"
    }
}