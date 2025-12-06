import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.sgtsilvio.gradle.proguard.ProguardTask

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    `java-gradle-plugin`
    `maven-publish`
    signing
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.gradleup.nmcp.aggregation") version "1.3.0"
    id("io.github.sgtsilvio.gradle.proguard") version "0.8.0"
}

group = "dev.keiji.license"
version = "0.0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")

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
            displayName = "Maven License Generator"
            description = "A Gradle plugin to generate License info."
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
                name = "Maven License Generator"
                description = "A Gradle plugin to generate License info."
                url = "https://github.com/keiji/gradle-license-generator"
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

nmcpAggregation {
    centralPortal {
        username = System.getenv("CENTRAL_PORTAL_USERNAME")
        password = System.getenv("CENTRAL_PORTAL_PASSWORD")
        publishingType = "USER_MANAGED"
    }

    publishAllProjectsProbablyBreakingProjectIsolation()
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

val proguardJar = tasks.register<ProguardTask>("proguardJar") {
    val javaHome = System.getProperty("java.home")
    jdkModules.add("java.base")
    jdkModules.add("java.sql")

    addInput {
        classpath.from(tasks.compileKotlin.get().outputs.files)
    }
    addLibrary {
        classpath.from(configurations.runtimeClasspath.get())
    }
    addOutput {
        archiveFile.set(layout.buildDirectory.file("libs/${rootProject.name}-${project.version}-proguard.jar"))
    }
    rules.addAll(project.file("proguard-rules.pro").readLines())
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
    archiveClassifier.set("all")
    from(proguardJar.get().outputs.files)
    dependsOn(proguardJar)

    from(sourceSets.main.get().output) {
        exclude("*")
    }
}
