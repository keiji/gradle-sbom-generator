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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
            version = "0.0.5"

            pom {
                name = "Maven License Generator Core"
                description = "Core library for Maven License Generator."
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
