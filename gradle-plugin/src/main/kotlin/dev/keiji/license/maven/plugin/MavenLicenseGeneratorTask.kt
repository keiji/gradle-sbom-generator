package dev.keiji.license.maven.plugin

import dev.keiji.license.maven.Generator
import dev.keiji.license.maven.entity.Settings
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

        val workingDir = extension.workingDir.orNull
            ?: throw IllegalArgumentException("`workingDir` must be set.")

        val localRepositoryDirs =
            if (extension.localRepositoryDirs.isPresent) {
                extension.localRepositoryDirs.get().map { it.absolutePath }
            } else {
                emptyList()
            }

        val outputSettings = extension.outputSettings.associate {
            val path = it.path.orNull?.absolutePath
                ?: throw IllegalArgumentException("`path` must be set for output '${it.name}'.")

            it.getName() to Settings.OutputSetting(
                path = path,
                override = it.override.get(),
                isPrettyPrintEnabled = it.prettyPrintEnabled.get()
            )
        }

        val settings = Settings(
            targetFilePath = targetFile.absolutePath,
            workingDir = workingDir.absolutePath,
            localRepositoryDirs = localRepositoryDirs,
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
