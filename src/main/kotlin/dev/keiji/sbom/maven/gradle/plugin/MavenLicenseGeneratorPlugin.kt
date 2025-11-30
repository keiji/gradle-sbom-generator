package dev.keiji.sbom.maven.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class MavenLicenseGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mavenLicenseGenerator", MavenLicenseGeneratorExtension::class.java)

        project.tasks.register("generateMavenLicense", MavenLicenseGeneratorTask::class.java) {
            it.extension = extension
        }
    }
}
