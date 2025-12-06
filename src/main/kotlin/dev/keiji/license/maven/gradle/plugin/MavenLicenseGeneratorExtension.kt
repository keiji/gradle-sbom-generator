package dev.keiji.license.maven.gradle.plugin

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.io.File
import javax.inject.Inject

abstract class MavenLicenseGeneratorExtension @Inject constructor(objects: ObjectFactory) {
    @get:Nested
    val targets: NamedDomainObjectContainer<TargetExtension> =
        objects.domainObjectContainer(TargetExtension::class.java) { name ->
            objects.newInstance(TargetExtension::class.java, name, objects)
        }

    @get:Input
    abstract val workingDir: Property<String>

    @get:Input
    abstract val localRepositoryDirs: ListProperty<String>

    @get:Input
    abstract val repositoryUrls: ListProperty<String>

    @get:Input
    abstract val removeConflictingVersions: Property<Boolean>

    @get:Input
    abstract val ignoreScopes: ListProperty<String>

    @get:Input
    abstract val includeDependencies: Property<Boolean>

    @get:Input
    abstract val includeSettings: Property<Boolean>

    @get:Nested
    val outputSettings: NamedDomainObjectContainer<OutputSettingExtension> =
        objects.domainObjectContainer(OutputSettingExtension::class.java) { name ->
            objects.newInstance(OutputSettingExtension::class.java, name, objects)
        }
}

open class TargetExtension @Inject constructor(private val name: String, objects: ObjectFactory) : Named {
    @Input
    override fun getName(): String = name

    @get:Input
    val configurations: ListProperty<String> = objects.listProperty(String::class.java)
}

open class OutputSettingExtension @Inject constructor(private val name: String, objects: ObjectFactory) : Named {
    @Input
    override fun getName(): String = name

    @get:Input
    @get:Optional
    val path: Property<String> = objects.property(String::class.java)

    @get:Input
    @get:Optional
    val file: Property<File> = objects.property(File::class.java)

    @get:Input
    val override: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    @get:Input
    val prettyPrintEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}
