plugins {
    id("dev.keiji.license.maven-license-generator")
    `java`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

mavenLicenseGenerator {
    workingDirFile = layout.buildDirectory.dir("license").get().asFile
    localRepositoryDirFiles = listOf(File(System.getProperty("user.home"), ".m2/repository"))
    repositoryUrls = listOf("https://repo1.maven.org/maven2")
    removeConflictingVersions = true
    ignoreScopes = listOf("test", "provided")
    includeDependencies = true
    includeSettings = true

    targets {
        create("main") {
            configurations = listOf("runtimeClasspath")
        }
    }

    outputSettings {
        // key must be "complete" or "incomplete" based on Generator implementation
        create("complete") {
            path = layout.buildDirectory.file("licenses.json").get().asFile.absolutePath
            prettyPrintEnabled = true
        }
    }
}
