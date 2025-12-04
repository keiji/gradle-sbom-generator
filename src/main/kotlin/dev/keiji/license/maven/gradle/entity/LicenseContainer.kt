package dev.keiji.license.maven.gradle.entity

import dev.keiji.license.maven.entity.Pom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LicenseContainer(

    @SerialName("settings")
    val settings: Settings? = null,

    @SerialName("pom_list")
    val pomList: List<Pom> = emptyList(),
)
