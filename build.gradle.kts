plugins {
    `java-library`
    `maven-publish`
}

allprojects {
    group = "io.github.ckaanf"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
        withJavadocJar()
    }

    tasks.test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("API Rate Limiter Library - ${project.name}")
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
                        developer {
                            id.set("user2")
                            name.set("user2")
                            email.set("user2")
                        }
                        developer {
                            id.set("user3")
                            name.set("user3")
                            email.set("user3")
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