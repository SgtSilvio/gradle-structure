package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Action
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import javax.inject.Inject

abstract class StructureExtension @Inject constructor(settings: Settings) {

    private var rootProjectName: String? = null
    private val rootProjectDefinition = ProjectDefinition(settings.rootProject, "", settings)

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

class ProjectDefinition(val descriptor: ProjectDescriptor, private val path: String, private val settings: Settings) {

    private val children = HashMap<String, ProjectDefinition>()

    private fun getOrAddChild(name: String): ProjectDefinition {
        return children.getOrPut(name) {
            val childPath = "$path:${descriptor.name}-$name"
            settings.include(childPath)
            val childDescriptor = settings.project(childPath)
            childDescriptor.projectDir = descriptor.projectDir.resolve(name)
            ProjectDefinition(childDescriptor, childPath, settings)
        }
    }

    fun project(name: String) {
        getOrAddChild(name)
    }

    fun project(name: String, configuration: Action<in ProjectDefinition>) {
        configuration.execute(getOrAddChild(name))
    }
}
