package dev.keiji.sbom.maven.gradle.entity

data class Library(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val depth: Int,
)
