dependencies {
    api(project(":core"))
    api("org.slf4j:slf4j-api:2.0.7")

    testImplementation("net.bytebuddy:byte-buddy:1.14.10")

}

tasks.processTestResources {
    exclude("META-INF/services/**")
}

