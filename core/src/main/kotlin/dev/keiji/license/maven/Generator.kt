package dev.keiji.license.maven

import dev.keiji.license.maven.PomParser
import dev.keiji.license.maven.entity.Pom
import dev.keiji.license.maven.entity.PomComparator
import dev.keiji.license.maven.entity.Library
import dev.keiji.license.maven.entity.LicenseContainer
import dev.keiji.license.maven.entity.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets

class Generator {
    fun generate(
        settings: Settings
    ) {
        val dependenciesFile = File(settings.targetFilePath)

        val libraryList = mutableListOf<Library>()

        dependenciesFile.readLines(charset = StandardCharsets.UTF_8).forEach { line ->
            val library = Library.parseLine(line) ?: return@forEach
            libraryList.add(library)
        }

        val workingDir = File(settings.workingDir).also {
            it.mkdirs()
        }

        val pomParser = PomParser()
        val processor = Processor(pomParser)

        val pomCache = mutableMapOf<String, Pom>()

        val pomList = libraryList.map {
            val pomFile = processor.downloadPom(
                settings.localRepositoryDirs,
                settings.repositoryUrls,
                it.groupId,
                it.artifactId,
                it.version,
                workingDir,
            )
            return@map if (pomFile == null) {
                Pom(it.groupId, it.artifactId, it.version)
            } else {
                pomParser.parseFile(pomFile, it.depth, null)
            }
        }.filterNotNull()

        pomList.forEach { pom ->
            if (pomCache.containsKey(pom.key)) {
                return@forEach
            }
            processor.downloadAllPom(
                settings.localRepositoryDirs,
                settings.repositoryUrls,
                settings.ignoreScopes,
                pom,
                pom.depth,
                workingDir,
                pomCache,
            )
        }

        var result = if (settings.removeConflictingVersions) {
            val dict = HashMap<String, Pom>()

            pomCache.values.sortedWith(PomComparator).forEach {
                val key = "${it.groupId}:${it.artifactId}"
                if (!dict.containsKey(key)) {
                    dict[key] = it
                }
            }
            dict.values.sortedWith(PomComparator)
        } else {
            pomCache.values.sortedWith(PomComparator)
        }

        result = if (settings.includeDependencies) {
            result
        } else {
            result.map { pom ->
                pom.also {
                    it.dependencies = emptyList()
                }
            }
        }

        val validList = result.filter {
            if (it.licenses.isEmpty()) {
                return@filter false
            }
            if (it.developers.isEmpty() && it.organization == null) {
                return@filter false
            }
            return@filter true
        }
        val invalidList = result.filter { !validList.contains(it) }

        settings.outputSettings["complete"]?.also { outputSettings ->
            val json = Json {
                prettyPrint = outputSettings.isPrettyPrintEnabled
                encodeDefaults = true
            }

            val licenseContainer = if (settings.includeSettings) {
                LicenseContainer(settings, validList)
            } else {
                LicenseContainer(null, validList)
            }

            val jsonText = json.encodeToString(licenseContainer)

            val outputFilePath = resolveOutputLocation(outputSettings.path, workingDir)

            saveFile(
                jsonText,
                outputFilePath.absolutePath,
                outputSettings.override
            )
        }

        settings.outputSettings["incomplete"]?.also { outputSettings ->
            val json = Json {
                prettyPrint = outputSettings.isPrettyPrintEnabled
                encodeDefaults = true
            }

            val licenseContainer = if (settings.includeSettings) {
                LicenseContainer(settings, invalidList)
            } else {
                LicenseContainer(null, invalidList)
            }

            val jsonText = json.encodeToString(licenseContainer)

            val outputFilePath = resolveOutputLocation(outputSettings.path, workingDir)

            saveFile(
                jsonText,
                outputFilePath.absolutePath,
                outputSettings.override
            )
        }
    }

    private fun resolveOutputLocation(
        path: String,
        workingDir: File
    ): File {
        val outputFile = File(path)
        return if (outputFile.isAbsolute) {
            outputFile
        } else {
            File(workingDir, path)
        }
    }

    private fun saveFile(content: String, filePath: String?, override: Boolean) {
        filePath ?: return

        val file = File(filePath)
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }

        if (!file.exists() || override) {
            file.outputStream().use {
                it.write(content.toByteArray(charset = StandardCharsets.UTF_8))
            }
        } else if (file.exists()) {
            val existContent = file.readText(charset = StandardCharsets.UTF_8)
            if (existContent != content) {
                println("*** ${file.name} is updated, but override-flag is not set.")
            }
        }
    }
}
