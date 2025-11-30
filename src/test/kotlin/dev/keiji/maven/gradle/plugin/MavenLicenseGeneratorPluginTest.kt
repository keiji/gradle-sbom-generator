package dev.keiji.maven.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import org.junit.jupiter.api.io.TempDir

class MavenLicenseGeneratorPluginTest {

    @Test
    fun `plugin registers task and runs successfully`(@TempDir tempDir: File) {
        val dependenciesFile = File(tempDir, "dependencies.txt")
        dependenciesFile.writeText("com.squareup.okhttp3:okhttp:4.10.0")

        val buildFile = File(tempDir, "build.gradle")
        buildFile.writeText("""
            plugins {
                id 'dev.keiji.maven-license-generator'
            }

            mavenLicenseGenerator {
                targetFilePath = 'dependencies.txt'
                workingDir = 'tmp'
                localRepositoryDirs = []
                repositoryUrls = ['https://repo1.maven.org/maven2']
                removeConflictingVersions = true
                ignoreScopes = ['test', 'runtime']
                includeDependencies = true
                includeSettings = false

                outputSettings.create("complete") {
                    path.set('sbom.json')
                    override.set(true)
                    prettyPrintEnabled.set(true)
                }
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(tempDir)
            .withPluginClasspath()
            .withArguments("generateMavenLicense")
            .build()

        println(result.output)

        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        assertTrue(File(tempDir, "sbom.json").exists())
    }
}
