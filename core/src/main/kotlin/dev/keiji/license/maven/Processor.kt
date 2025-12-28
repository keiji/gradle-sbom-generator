package dev.keiji.license.maven

import dev.keiji.license.maven.PomParser
import dev.keiji.license.maven.entity.Pom
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import kotlin.random.Random

internal class Processor {
    private val rand = Random(System.currentTimeMillis())

    private val client: OkHttpClient = OkHttpClient()

    internal fun downloadAllPom(
        localRepositoryDirs: List<String>,
        repositoryUrls: List<String>,
        ignoreScopes: List<String>,
        pom: Pom,
        depth: Int,
        workingDir: File,
        pomCache: MutableMap<String, Pom>,
    ): Pom? {
        val parentPom = pom.parent?.let {
            if (pomCache.contains(it.key)) {
                pomCache[it.key]
            } else {
                downloadAllPom(
                    localRepositoryDirs,
                    repositoryUrls,
                    ignoreScopes,
                    it,
                    depth + 1,
                    workingDir,
                    pomCache
                )
            }
        }

        val pomFile = downloadPom(
            localRepositoryDirs, repositoryUrls, pom.groupId, pom.artifactId, pom.version, workingDir
        ) ?: return null
        val pom = PomParser().parseFile(pomFile, depth + 1, parentPom) ?: return null

        pomCache[pom.key] = pom

        val props = mutableMapOf<String, String>().also {
            pom.getAllProperties(it)
        }

        pom.name = pom.name?.replaceProperties(props)

        pom.dependencies.forEach {
            it.groupId = it.groupId.replaceProperties(props)
            it.artifactId = it.artifactId.replaceProperties(props)
            it.version = processVersionRange(it.version.replaceProperties(props)) ?: return@forEach

            val dependencyPom = Pom(it.groupId, it.artifactId, it.version)

            if (pomCache.contains(dependencyPom.key)) {
                return@forEach
            }

            if (ignoreScopes.contains(it.scope)) {
                return@forEach
            }

            pomCache[dependencyPom.key] = downloadAllPom(
                localRepositoryDirs,
                repositoryUrls,
                ignoreScopes,
                dependencyPom,
                depth + 1,
                workingDir,
                pomCache
            ) ?: return@forEach
        }

        return pom
    }

    internal fun downloadPom(
        localRepositoryDirs: List<String>,
        repositoryUrls: List<String>,
        groupId: String,
        artifactId: String,
        version: String,
        workingDir: File,
        dryRun: Boolean = false,
    ): File? {
        val groupPath = groupId.replace('.', '/')
        val fileName = "${artifactId}-${version}.pom"

        val pomDir = "${groupPath}/${artifactId}/${version}"
        val pomPath = File(workingDir, pomDir).also {
            it.mkdirs()
        }

        val filePath = File(pomPath, fileName)
        if (filePath.exists()) {
            return filePath
        }

        localRepositoryDirs.filter { it.isNotEmpty() }.forEach { path ->
            val pomPath = File(path, pomDir)
            val filePath = File(pomPath, fileName)
            if (filePath.exists()) {
                return filePath
            }
        }

        if (groupId.contains("$")) {
            println("variable detected in groupId ${groupId}.")
            return null
        }
        if (artifactId.contains("$")) {
            println("variable detected in artifactId ${artifactId}.")
            return null
        }
        if (version.contains("$")) {
            println("variable detected in version ${version}.")
            return null
        }

        println("Downloading ${groupId}:${artifactId}:${version}...")

        if (dryRun) {
            return null
        }

        repositoryUrls.filter { it.isNotEmpty() }.forEach {
            val url = "${it}/${pomDir}/${fileName}"
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            response.use { response ->
                when (response.code) {
                    HttpURLConnection.HTTP_OK -> {
                        filePath.outputStream().use {
                            response.body?.byteStream()?.copyTo(it)
                        }
                        return filePath
                    }

                    else -> {
                        println("$url status code: ${response.code}")
                    }
                }
                Thread.sleep(rand.nextLong(1000))
            }
        }

        return null
    }
}

private fun String.replaceProperties(properties: Map<String, String>): String {
    var result: String = this
    properties.keys.forEach {
        val key = it.split(":").last()
        val placeholder = "\${$key}"
        val prop = properties[it] ?: return@forEach
        if (result.contains(placeholder)) {
            result = result.replace(placeholder, prop)
        }
    }

    return result
}

private val REGEX_VERSION_SELECTOR = "\\[(.*?)]".toRegex()
private val REGEX_VERSION_RANGE = "\\[(.*?)\\)".toRegex()

private fun processVersionRange(versionText: String?): String? {
    versionText ?: return null

    if (!versionText.startsWith("[")) {
        return versionText
    }

    if (versionText.endsWith("]")) {
        val matches = REGEX_VERSION_SELECTOR.findAll(versionText)
        return matches.first().groupValues[1]
    }

    if (versionText.endsWith(")")) {
        val matches = REGEX_VERSION_RANGE.findAll(versionText)
        return matches.first().groupValues[1].split(",")
            .map { it.trim() }
            .last { it.isNotEmpty() }
    }

    return null
}
