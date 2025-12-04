package dev.keiji.license.maven.gradle

import dev.keiji.license.maven.gradle.entity.Settings
import kotlinx.serialization.decodeFromString
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

    val currentPath = File(System.getProperty("user.dir"))

    Generator().generate(settings, currentPath)

    println("Finished.")
}
