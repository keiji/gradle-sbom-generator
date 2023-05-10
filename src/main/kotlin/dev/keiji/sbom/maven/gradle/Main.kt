package dev.keiji.sbom.maven.gradle

import dev.keiji.sbom.maven.PomParser
import dev.keiji.sbom.maven.entity.Pom
import dev.keiji.sbom.maven.entity.PomComparator
import dev.keiji.sbom.maven.gradle.entity.Library
import dev.keiji.sbom.maven.gradle.entity.SbomContainer
import dev.keiji.sbom.maven.gradle.entity.Settings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("settingFilePath argument must be set.")
        return
    }

    val settingFile = File(args[0])
    if (!settingFile.exists()) {
        println("${settingFile.absolutePath} is not exist.")
        return
    }

    val settings: Settings = settingFile.readText(charset = StandardCharsets.UTF_8).let {
        Json.decodeFromString(it)
    }

    val dependenciesFile = File(settings.targetFilePath)

    val libraryList = mutableListOf<Library>()

    dependenciesFile.readLines(charset = StandardCharsets.UTF_8).forEach { line ->
        val library = Library.parseLine(line) ?: return@forEach
        libraryList.add(library)
    }

    val workingDir = File(settings.workingDir).also {
        it.mkdirs()
    }

    val processor = Processor()

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
            PomParser().parseFile(pomFile, it.depth)
        }
    }.filterNotNull()

    pomList.forEach {
        pomCache[it.key] = it
    }

    pomList.forEach { pom ->
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

        val sbom = if (settings.includeSettings) {
            SbomContainer(settings, validList)
        } else {
            SbomContainer(null, validList)
        }

        val jsonText = json.encodeToString(sbom)

        saveFile(
            jsonText,
            outputSettings.path,
            outputSettings.override
        )
    }

    settings.outputSettings["incomplete"]?.also { outputSettings ->
        val json = Json {
            prettyPrint = outputSettings.isPrettyPrintEnabled
            encodeDefaults = true
        }

        val sbom = if (settings.includeSettings) {
            SbomContainer(settings, invalidList)
        } else {
            SbomContainer(null, invalidList)
        }

        val jsonText = json.encodeToString(sbom)

        saveFile(
            jsonText,
            outputSettings.path,
            outputSettings.override
        )
    }

    println("Finished.")
}

fun saveFile(content: String, filePath: String?, override: Boolean) {
    filePath ?: return

    val file = File(filePath)
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
