package dev.keiji.license.maven.gradle.plugin

import dev.keiji.license.maven.gradle.Generator
import dev.keiji.license.maven.gradle.entity.Settings
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class MavenLicenseGeneratorTask @Inject constructor() : DefaultTask() {

    @get:Nested
    lateinit var extension: MavenLicenseGeneratorExtension

    @get:InputFile
    abstract val dependenciesFile: RegularFileProperty

    @get:Internal
    abstract val projectDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val targetFile = dependenciesFile.get().asFile

        val workingDir = projectDirectory.dir(extension.workingDir.get()).get().asFile

        val outputSettings = extension.outputSettings.associate {
            val path = when {
                it.file.isPresent -> it.file.get().absolutePath
                it.path.isPresent -> it.path.get()
                else -> throw IllegalArgumentException("Either 'path' or 'file' must be set for output '${it.name}'.")
            }
            it.getName() to Settings.OutputSetting(
                path = path,
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
