dependencies {
    api(project(":core"))
    api(project(":algorithms:token-bucket"))

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.processTestResources {
    exclude("META-INF/services/**")
}




