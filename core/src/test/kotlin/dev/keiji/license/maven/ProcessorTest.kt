package dev.keiji.license.maven

import dev.keiji.license.maven.entity.Pom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class ProcessorTest {

    @Test
    fun parseNestedPomTest() {
        val pomCache = mutableMapOf<String, Pom>()

        val pomParser = PomParser()

        val childPomFile = File("src/test/resources/poms/child.xml")
        val parentPomFile = File("src/test/resources/poms/parent.xml")
        val grandparentPomFile = File("src/test/resources/poms/grandparent.xml")

        val processor = object : Processor(pomParser) {
            override fun downloadPom(
                localRepositoryDirs: List<String>,
                repositoryUrls: List<String>,
                groupId: String,
                artifactId: String,
                version: String,
                workingDir: File,
                dryRun: Boolean
            ): File? {
                return when (artifactId) {
                    "child" -> childPomFile
                    "parent" -> parentPomFile
                    "grandparent" -> grandparentPomFile
                    else -> null
                }
            }
        }

        val initialPom = pomParser.parseFile(childPomFile, 0, null)!!

        val resultPom = processor.downloadAllPom(
            emptyList(),
            emptyList(),
            emptyList(),
            initialPom,
            0,
            File(""),
            pomCache
        )

        assertNotNull(resultPom)
        resultPom!!

        assertEquals("The Apache Software License, Version 2.0", resultPom.licenses.first().name)
        assertEquals("Keiji", resultPom.developers.first().name)
        assertEquals(2, resultPom.dependencies.size)
        assertNotNull(resultPom.dependencies.find { it.artifactId == "kotlinx-coroutines-core" })
        assertNotNull(resultPom.dependencies.find { it.artifactId == "kotlin-stdlib" })
    }
}
