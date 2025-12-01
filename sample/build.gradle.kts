plugins {
    id("dev.keiji.maven-license-generator")
    `java`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

mavenLicenseGenerator {
    workingDir.set(layout.buildDirectory.dir("sbom").get().asFile.absolutePath)
    localRepositoryDirs.set(listOf(System.getProperty("user.home") + "/.m2/repository"))
    repositoryUrls.set(listOf("https://repo1.maven.org/maven2"))
    removeConflictingVersions.set(true)
    ignoreScopes.set(listOf("test", "provided"))
    includeDependencies.set(true)
    includeSettings.set(true)

    targets {
        create("main") {
            configurations.add("runtimeClasspath")
        }
    }

    outputSettings {
        // key must be "complete" or "incomplete" based on Generator implementation
        create("complete") {
            path.set(layout.buildDirectory.file("licenses.json").get().asFile.absolutePath)
            prettyPrintEnabled.set(true)
        }
    }
}
