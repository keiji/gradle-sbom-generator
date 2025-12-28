plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
    signing
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.1")
    testImplementation("org.mockito:mockito-inline:3.12.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "dev.keiji.license"
            artifactId = "core"

            pom {
                name = "Maven License Generator Core"
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
