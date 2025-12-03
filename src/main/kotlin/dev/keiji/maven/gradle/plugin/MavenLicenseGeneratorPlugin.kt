package dev.keiji.maven.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.diagnostics.DependencyReportTask

class MavenLicenseGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mavenLicenseGenerator", MavenLicenseGeneratorExtension::class.java)

        val collectTask = project.tasks.register("collectDependenciesForMavenLicense", DependencyReportTask::class.java)

        val generateTask = project.tasks.register("generateMavenLicense", MavenLicenseGeneratorTask::class.java) {
            it.extension = extension
            it.projectDirectory.set(project.layout.projectDirectory)
        }

        project.afterEvaluate {
            val configurationNames = extension.targets.flatMap { it.configurations.get() }.toSet()

            if (configurationNames.isNotEmpty()) {
                val configurations = configurationNames.mapNotNull { name ->
                    project.configurations.findByName(name)
                }.toSet()

                val outputFile = project.layout.buildDirectory.file("maven-license-generator/dependencies.txt")

                collectTask.configure {
                    it.configurations = configurations
                    it.setOutputFile(outputFile.get().asFile)
                }

                generateTask.configure {
                    it.dependsOn(collectTask)
                    it.dependenciesFile.set(outputFile)
                }
            }
        }
    }
}
