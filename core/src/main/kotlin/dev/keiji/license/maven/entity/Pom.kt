package dev.keiji.license.maven.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Comparator

object PomComparator : Comparator<Pom> {
    override fun compare(obj1: Pom, obj2: Pom): Int {
        if (obj1.depth.compareTo(obj2.depth) != 0) {
            return obj1.depth.compareTo(obj2.depth)
        }
        if (obj1.groupId.compareTo(obj2.groupId) != 0) {
            return obj1.groupId.compareTo(obj2.groupId)
        }
        if (obj1.artifactId.compareTo(obj2.artifactId) != 0) {
            return obj1.artifactId.compareTo(obj2.artifactId)
        }
        if (obj1.version.compareTo(obj2.version) != 0) {
            return obj1.version.compareTo(obj2.version) * -1
        }
        return 0
    }
}

@Serializable
data class Pom(
    @SerialName("group_id")
    val groupId: String,
    @SerialName("artifact_id")
    val artifactId: String,
    @SerialName("version")
    val version: String,
) {
    fun getAllProperties(props: MutableMap<String, String>) {
        properties.keys.forEach {
            if (!props.contains(it)) {
                val value = properties[it] ?: return@forEach
                props[it] = value
            }
        }

        parent?.getAllProperties(props)
    }

    val key: String
        get() = "${groupId}:${artifactId}:${version}"

    @SerialName("name")
    var name: String? = null

    @SerialName("url")
    var url: String? = null

    @SerialName("licenses")
    var licenses: List<License> = emptyList()

    @SerialName("developers")
    var developers: List<Developer> = emptyList()

    @SerialName("organization")
    var organization: Organization? = null

    @SerialName("dependencies")
    var dependencies: List<Dependency> = emptyList()

    @SerialName("depth")
    var depth: Int = 0

    @Transient
    var parent: Pom? = null

    @Transient
    var properties: Map<String, String> = emptyMap()

    fun merge(parent: Pom) {
        if (name == null) {
            name = parent.name
        }
        if (url == null) {
            url = parent.url
        }
        if (licenses.isEmpty()) {
            licenses = parent.licenses
        }
        if (developers.isEmpty()) {
            developers = parent.developers
        }
        if (organization == null) {
            organization = parent.organization
        }

        val dependencyKeys = dependencies.map { it.key }.toSet()
        val mergedDependencies = mutableListOf<Dependency>().also {
            it.addAll(dependencies)
        }
        parent.dependencies.forEach {
            if (!dependencyKeys.contains(it.key)) {
                mergedDependencies.add(it)
            }
        }
        dependencies = mergedDependencies
    }

    @Serializable
    data class License(val name: String, val url: String?)

    @Serializable
    data class Developer(val name: String, val url: String?)

    @Serializable
    data class Organization(
        @SerialName("name")
        val name: String,

        @SerialName("url")
        val url: String?,
    )

    @Serializable
    data class Dependency(
        @SerialName("group_id")
        var groupId: String,

        @SerialName("artifact_id")
        var artifactId: String,

        @SerialName("version")
        var version: String,

        @SerialName("scope")
        var scope: String? = null,
    ) {
        val key: String
            get() = "$groupId:$artifactId"
    }

}
