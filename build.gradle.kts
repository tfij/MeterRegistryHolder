plugins {
    java
}

group = "pl.tfij"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micrometer:micrometer-core:1.8.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-all:1.10.19")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}