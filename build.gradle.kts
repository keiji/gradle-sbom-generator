plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    `java-gradle-plugin`
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "dev.keiji.sbom"
version = "0.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    testImplementation(kotlin("test"))
    gradleApi()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("dev.keiji.sbom.maven.gradle.MainKt")
}

gradlePlugin {
    plugins {
        create("mavenLicenseGenerator") {
            id = "dev.keiji.maven-license-generator"
            implementationClass = "dev.keiji.maven.gradle.plugin.MavenLicenseGeneratorPlugin"
        }
    }
}
