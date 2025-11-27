package dev.keiji.sbom.maven.gradle.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    @SerialName("target_file_path")
    val targetFilePath: String,

    @SerialName("working_dir")
    val workingDir: String,

    @SerialName("local_repository_dirs")
    val localRepositoryDirs: List<String>,

    @SerialName("repository_urls")
    val repositoryUrls: List<String>,

    @SerialName("remove_conflicting_versions")
    val removeConflictingVersions: Boolean,

    @SerialName("ignore_scopes")
    val ignoreScopes: List<String>,

    @SerialName("include_dependencies")
    val includeDependencies: Boolean,

    @SerialName("include_settings")
    val includeSettings: Boolean,

    @SerialName("output")
    val outputSettings: Map<String, OutputSetting>
) {
    @Serializable
    data class OutputSetting(
        @SerialName("path")
        val path: String,

        @SerialName("override")
        val override: Boolean = true,

        @SerialName("is_pretty_print_enabled")
        val isPrettyPrintEnabled: Boolean,

        @SerialName("format")
        val format: String = "legacy",
    )
}
