package dev.keiji.sbom.maven.gradle.entity

data class Library(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val depth: Int,
) {
    companion object {

        private const val LINE_MARKER = "--- "

        fun parseLine(line: String): Library? {
            if (!line.contains(LINE_MARKER)) {
                return null
            }

            val index = line.indexOf(LINE_MARKER)
            val data = line.substring(index + LINE_MARKER.length).trim()
            val splitted = data.split(":").map { it.trim() }

            if (splitted.first() == "project") {
                return null
            }

            val depth = line.count { it == '|' }

            if (splitted.size == 2) {
                // |    +--- androidx.compose.ui:ui -> 1.5.0-alpha03
                val groupId = splitted[0]
                val artifactId = splitted[1].split(" -> ").first().trim()
                val version = processVersion(splitted[1])
                return Library(groupId, artifactId, version, depth)
            } else {
                // +--- com.google.android.gms:play-services-wearable:18.0.0
                val groupId = splitted[0]
                val artifactId = splitted[1]
                val version = processVersion(splitted[2])
                return Library(groupId, artifactId, version, depth)
            }
        }

        internal fun processVersion(versionStr: String): String {
            val index = versionStr.lastIndexOf("(")
            val versions = if (index > -1) {
                versionStr.substring(0, index)
            } else {
                versionStr
            }

            return versions.split("->").last().trim()
        }

    }
}
