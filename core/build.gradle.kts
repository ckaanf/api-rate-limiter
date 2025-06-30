dependencies {
    api("org.slf4j:slf4j-api:2.0.7")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.bytebuddy:byte-buddy:1.14.10")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.1.1")
    testImplementation("org.assertj:assertj-core:3.24.2")

}

java {
    modularity.inferModulePath.set(true)
}