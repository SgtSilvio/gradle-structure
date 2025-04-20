package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Action
import org.gradle.api.NonExtensible
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@NonExtensible
abstract class StructureExtension @Inject constructor(settings: Settings, objectFactory: ObjectFactory) {

    private var rootProjectName: String? = null
    internal val rootProjectDefinition = objectFactory.newInstance<ProjectDefinition>(settings.rootProject, settings)

    fun rootProject(name: String, configuration: Action<in ProjectDefinition>) {
        if (rootProjectName == null) {
            rootProjectName = name
            rootProjectDefinition.descriptor.name = name
        } else if (rootProjectName != name) {
            throw IllegalStateException("rootProject name must always be the same, expected $rootProjectName, got $name")
        }
        configuration.execute(rootProjectDefinition)
    }
}

@NonExtensible
abstract class ProjectDefinition @Inject constructor(
    val descriptor: ProjectDescriptor,
    private val settings: Settings,
    private val objectFactory: ObjectFactory,
) {

    internal val children = HashMap<String, ProjectDefinition>()

    private fun getOrAddChild(name: String): ProjectDefinition {
        return children.getOrPut(name) {
            val childPath = "${if (descriptor.path == ":") "" else descriptor.path}:${descriptor.name}-$name"
            settings.include(childPath)
            val childDescriptor = settings.project(childPath)
            childDescriptor.projectDir = descriptor.projectDir.resolve(name)
            objectFactory.newInstance<ProjectDefinition>(childDescriptor, settings)
        }
    }

    fun project(name: String) {
        getOrAddChild(name)
    }

    fun project(name: String, configuration: Action<in ProjectDefinition>) {
        configuration.execute(getOrAddChild(name))
    }
}
