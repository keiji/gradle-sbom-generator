package dev.keiji.sbom.maven.gradle.entity

import dev.keiji.sbom.maven.entity.Pom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SbomContainer(

    @SerialName("settings")
    val settings: Settings? = null,

    @SerialName("pom_list")
    val pomList: List<Pom> = emptyList(),
)
