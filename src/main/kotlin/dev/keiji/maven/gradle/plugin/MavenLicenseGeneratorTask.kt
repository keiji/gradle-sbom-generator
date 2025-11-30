package dev.keiji.maven.gradle.plugin

import dev.keiji.sbom.maven.gradle.Generator
import dev.keiji.sbom.maven.gradle.entity.Settings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class MavenLicenseGeneratorTask @Inject constructor() : DefaultTask() {

    @get:Nested
    lateinit var extension: MavenLicenseGeneratorExtension

    @TaskAction
    fun generate() {
        val targetFile = project.file(extension.targetFilePath.get())
        val workingDir = project.file(extension.workingDir.get())

        val outputSettings = extension.outputSettings.associate {
            val file = project.file(it.path.get())
            it.getName() to Settings.OutputSetting(
                path = file.absolutePath,
                override = it.override.get(),
                isPrettyPrintEnabled = it.prettyPrintEnabled.get()
            )
        }

        val settings = Settings(
            targetFilePath = targetFile.absolutePath,
            workingDir = workingDir.absolutePath,
            localRepositoryDirs = extension.localRepositoryDirs.get(),
            repositoryUrls = extension.repositoryUrls.get(),
            removeConflictingVersions = extension.removeConflictingVersions.get(),
            ignoreScopes = extension.ignoreScopes.get(),
            includeDependencies = extension.includeDependencies.get(),
            includeSettings = extension.includeSettings.get(),
            outputSettings = outputSettings
        )

        Generator().generate(settings)
    }
}
