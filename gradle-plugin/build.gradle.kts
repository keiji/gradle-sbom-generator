plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    signing
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(project(":core"))
    gradleApi()
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    testImplementation(gradleTestKit())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("mavenLicenseGenerator") {
            id = "dev.keiji.license.maven-license-generator"
            displayName = "Maven License Generator"
            description = "A Gradle plugin to generate License info."
            implementationClass = "dev.keiji.license.maven.plugin.MavenLicenseGeneratorPlugin"
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            groupId = "dev.keiji.license"
            artifactId = "gradle-license-generator"
            version = "0.0.5"

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

signing {
    useGpgCmd()
    sign(publishing.publications)
}
