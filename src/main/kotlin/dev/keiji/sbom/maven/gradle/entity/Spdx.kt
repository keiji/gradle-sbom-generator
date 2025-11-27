package dev.keiji.sbom.maven.gradle.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpdxDocument(
    @SerialName("spdxVersion") val spdxVersion: String = "SPDX-2.3",
    @SerialName("dataLicense") val dataLicense: String = "CC0-1.0",
    @SerialName("SPDXID") val spdxId: String = "SPDXRef-DOCUMENT",
    @SerialName("name") val name: String,
    @SerialName("documentNamespace") val documentNamespace: String,
    @SerialName("creationInfo") val creationInfo: CreationInfo,
    @SerialName("packages") val packages: List<SpdxPackage>,
    @SerialName("relationships") val relationships: List<SpdxRelationship>,
)

@Serializable
data class CreationInfo(
    @SerialName("creators") val creators: List<String>,
    @SerialName("created") val created: String, // YYYY-MM-DDThh:mm:ssZ
)

@Serializable
data class SpdxPackage(
    @SerialName("name") val name: String,
    @SerialName("SPDXID") val spdxId: String,
    @SerialName("versionInfo") val versionInfo: String,
    @SerialName("downloadLocation") val downloadLocation: String = "NOASSERTION",
    @SerialName("filesAnalyzed") val filesAnalyzed: Boolean = false,
    @SerialName("licenseConcluded") val licenseConcluded: String = "NOASSERTION",
    @SerialName("licenseDeclared") val licenseDeclared: String = "NOASSERTION",
    @SerialName("copyrightText") val copyrightText: String = "NOASSERTION",
    @SerialName("supplier") val supplier: String = "NOASSERTION",
)

@Serializable
data class SpdxRelationship(
    @SerialName("spdxElementId") val spdxElementId: String,
    @SerialName("relatedSpdxElement") val relatedSpdxElement: String,
    @SerialName("relationshipType") val relationshipType: String,
)
