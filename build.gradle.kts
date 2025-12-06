plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "2.2.21"
    `java-gradle-plugin`
    `maven-publish`
    signing
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.gradleup.nmcp") version "0.0.8"
}

group = "dev.keiji.license"
version = "0.0.4"
description = "A Gradle plugin to generate License info."

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

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
    mainClass.set("dev.keiji.license.maven.gradle.MainKt")
}

gradlePlugin {
    plugins {
        create("mavenLicenseGenerator") {
            id = "dev.keiji.license.maven-license-generator"
            implementationClass = "dev.keiji.license.maven.gradle.plugin.MavenLicenseGeneratorPlugin"
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
                description.set("A Gradle plugin to generate License info.")
                url = "https://github.com/keiji/gradle-license-generator"
                withXml {
                    val root = asNode()
                    if ((root.get("name") as List<*>).isEmpty()) {
                        root.appendNode("name", "Maven License Generator")
                    }
                    if ((root.get("description") as List<*>).isEmpty()) {
                        root.appendNode("description", "A Gradle plugin to generate License info.")
                    }
                }
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "keiji"
                        name = "ARIYAMA Keiji"
                        email = "keiji.ariyama@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/keiji/gradle-license-generator.git"
                    developerConnection = "scm:git:ssh://git@github.com/keiji/gradle-license-generator.git"
                    url = "https://github.com/keiji/gradle-license-generator"
                }
            }
        }
    }
}

nmcp {
    publishAllPublications {
        username = System.getenv("CENTRAL_PORTAL_USERNAME")
        password = System.getenv("CENTRAL_PORTAL_PASSWORD")
        publicationType = "USER_MANAGED"
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    } else {
        useGpgCmd()
    }
    sign(publishing.publications)
}
