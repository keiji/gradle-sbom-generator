plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    `java-gradle-plugin`
    `maven-publish`
    signing
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.keiji.sbom"
version = "0.0.4"

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

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kotlin {
    jvmToolchain(21)
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

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("Maven License Generator")
                description.set("A Gradle plugin to generate SBOM/License info.")
                url.set("https://github.com/keiji/gradle-sbom-generator")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("keiji")
                        name.set("Keiji Ariyama")
                        email.set("keiji.ariyama@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/keiji/gradle-sbom-generator.git")
                    developerConnection.set("scm:git:ssh://github.com/keiji/gradle-sbom-generator.git")
                    url.set("https://github.com/keiji/gradle-sbom-generator")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
