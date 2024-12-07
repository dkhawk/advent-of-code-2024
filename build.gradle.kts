plugins {
    kotlin("jvm") version "2.1.0"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}

group = "com.sphericalchickens"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.jsoup:jsoup:1.18.3")
//    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1") // Replace with the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.1") // Replace with the latest version

    testImplementation(kotlin("test"))
}
