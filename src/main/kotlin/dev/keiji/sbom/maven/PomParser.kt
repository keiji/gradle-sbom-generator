package dev.keiji.sbom.maven

import dev.keiji.sbom.maven.entity.Pom
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXParseException
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class PomParser {
    internal fun parseFile(
        file: File,
        depth: Int,
    ): Pom? {
        val document = try {
            DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(file) ?: return null
        } catch (exception: SAXParseException) {
            println("${file.absolutePath}, ${exception.message}")
            return null
        }

        val xpath = XPathFactory.newInstance().newXPath()

        val projectNodeList = xpath.compile("/project")
            .evaluate(document, XPathConstants.NODESET) as NodeList

        if (projectNodeList.length == 0) {
            return null
        }

        val projectElement = projectNodeList.item(0) as Element

        val parentNodeList = xpath.compile("parent")
            .evaluate(projectElement, XPathConstants.NODESET) as NodeList

        val parent = if (parentNodeList.length > 0) {
            val parentElement = parentNodeList.item(0) as Element
            val parentGroupId = getTextByTagName(xpath, "groupId", parentElement)
            val parentArtifactId = getTextByTagName(xpath, "artifactId", parentElement)
            val parentVersion = getTextByTagName(xpath, "version", parentElement)
            if (parentGroupId == null || parentArtifactId == null || parentVersion == null) {
                null
            } else {
                Pom(parentGroupId.trim(), parentArtifactId.trim(), parentVersion.trim())
            }
        } else {
            null
        }

        var projectGroupId = getTextByTagName(xpath, "groupId", projectElement)
        if (projectGroupId == null) {
            projectGroupId = parent?.groupId
        }

        var projectArtifactId = getTextByTagName(xpath, "artifactId", projectElement)
        if (projectArtifactId == null) {
            projectArtifactId = parent?.artifactId
        }

        var projectVersion = getTextByTagName(xpath, "version", projectElement)
        if (projectVersion == null) {
            projectVersion = parent?.version
        }

        val name = getTextByTagName(xpath, "name", projectElement)
        val url = getTextByTagName(xpath, "url", projectElement)

        if (projectGroupId == null) {
            println("*** POM ${file.absolutePath} doesn't contain `groupId`.")
            return null
        }
        if (projectArtifactId == null) {
            println("*** POM ${file.absolutePath} doesn't contain `artifactId`.")
            return null
        }
        if (projectVersion == null) {
            println("*** POM ${file.absolutePath} doesn't contain `version`.")
            return null
        }

        val keyPrefix = "${projectGroupId}:${projectArtifactId}:${projectVersion}"

        val propertiesMap = mutableMapOf<String, String>().also {
            it["${keyPrefix}:pom.groupId"] = projectGroupId
            it["${keyPrefix}:pom.artifactId"] = projectArtifactId
            it["${keyPrefix}:pom.version"] = projectVersion

            it["${keyPrefix}:project.groupId"] = projectGroupId
            it["${keyPrefix}:project.artifactId"] = projectArtifactId
            it["${keyPrefix}:project.version"] = projectVersion

            it["${keyPrefix}:groupId"] = projectGroupId
            it["${keyPrefix}:artifactId"] = projectArtifactId
            it["${keyPrefix}:version"] = projectVersion
        }

        if (parent != null) {
            propertiesMap["${keyPrefix}:parent.groupId"] = parent.groupId
            propertiesMap["${keyPrefix}:parent.artifactId"] = parent.artifactId
            propertiesMap["${keyPrefix}:parent.version"] = parent.version
        }

        readProperties(projectGroupId, projectArtifactId, projectVersion, xpath, projectElement, propertiesMap)

        val result = Pom(
            groupId = projectGroupId.trim(),
            artifactId = projectArtifactId.trim(),
            version = projectVersion.trim(),
        ).also {
            it.name = name
            it.url = url?.trim()
            it.depth = depth
            it.parent = parent
            it.properties = propertiesMap
        }

        val licenseNodeList = xpath.compile("licenses/license")
            .evaluate(projectElement, XPathConstants.NODESET) as NodeList

        result.licenses = (0 until licenseNodeList.length).map {
            val element = licenseNodeList.item(it) as Element
            val licenseName = getTextByTagName(xpath, "name", element)
            val licenseUrl = getTextByTagName(xpath, "url", element)

            licenseName ?: return@map null
            return@map Pom.License(licenseName, licenseUrl?.trim())
        }.filterNotNull().toList()

        val developerNodeList = xpath.compile("developers/developer")
            .evaluate(projectElement, XPathConstants.NODESET) as NodeList

        result.developers = (0 until developerNodeList.length).map {
            val element = developerNodeList.item(it) as Element
            val developerName = getTextByTagName(xpath, "name", element)
            val developerUrl = getTextByTagName(xpath, "url", element)

            developerName ?: return@map null
            return@map Pom.Developer(developerName, developerUrl?.trim())
        }.filterNotNull().toList()

        val organizationNodeList = xpath.compile("organization")
            .evaluate(projectElement, XPathConstants.NODESET) as NodeList

        if (organizationNodeList.length > 0) {
            val element = organizationNodeList.item(0) as Element
            val organizationName = getTextByTagName(xpath, "name", element)
            val organizationUrl = getTextByTagName(xpath, "url", element)

            if (organizationName != null) {
                result.organization = Pom.Organization(organizationName, organizationUrl?.trim())
            }
        }

        val dependenciesNodeList = xpath.compile("dependencies/dependency")
            .evaluate(projectElement, XPathConstants.NODESET) as NodeList

        result.dependencies = (0 until dependenciesNodeList.length).map {
            val element = dependenciesNodeList.item(it) as Element
            val dependencyGroupId = getTextByTagName(xpath, "groupId", element)
            val dependencyArtifactId = getTextByTagName(xpath, "artifactId", element)
            val dependencyVersion = getTextByTagName(xpath, "version", element)
            val dependencyScope = getTextByTagName(xpath, "scope", element)

            dependencyGroupId ?: return@map null
            dependencyArtifactId ?: return@map null
            dependencyVersion ?: return@map null

            return@map Pom.Dependency(
                dependencyGroupId.trim(),
                dependencyArtifactId.trim(),
                dependencyVersion.trim(),
                dependencyScope,
            )
        }.filterNotNull().toList()

        return result
    }
}

private fun readProperties(
    groupId: String,
    artifactId: String,
    version: String,
    xpath: XPath,
    projectElement: Element,
    outMap: MutableMap<String, String>,
) {
    val propertiesElements = xpath.compile("properties")
        .evaluate(projectElement, XPathConstants.NODESET) as NodeList
    if (propertiesElements.length == 0) {
        return
    }

    val propertiesNode = propertiesElements.item(0)

    (0 until propertiesNode.childNodes.length).forEach { index ->
        val node = propertiesNode.childNodes.item(index)
        if (node.nodeType == Node.ELEMENT_NODE) {
            val key = "${groupId}:${artifactId}:${version}:${node.nodeName}"
            outMap[key] = node.textContent
        }
    }
}

private fun getTextByTagName(xpath: XPath, tag: String, element: Element): String? {
    val elements = xpath.compile(tag)
        .evaluate(element, XPathConstants.NODESET) as NodeList
    if (elements.length == 0) {
        return null
    }

    val innerElement = elements.item(0) as Element
    return innerElement.textContent
}
