plugins {
    `java-library`
}

dependencies {
    api("org.slf4j:slf4j-api:2.0.7")
}

java {
    modularity.inferModulePath.set(true)
}