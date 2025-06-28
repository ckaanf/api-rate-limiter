plugins {
    `java-library`
    `maven-publish`
}

allprojects {
    group = "io.github.yourusername"
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

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.7")

        testImplementation(platform("org.junit:junit-bom:5.9.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.assertj:assertj-core:3.24.2")
        testImplementation("org.mockito:mockito-core:5.3.1")
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
                    url.set("https://github.com/yourusername/api-rate-limiter")

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
                        connection.set("scm:git:git://github.com/yourusername/api-rate-limiter.git")
                        developerConnection.set("scm:git:ssh://github.com/yourusername/api-rate-limiter.git")
                        url.set("https://github.com/yourusername/api-rate-limiter")
                    }
                }
            }
        }
    }
}