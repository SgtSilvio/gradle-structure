package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Action
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import javax.inject.Inject

abstract class StructureExtension @Inject constructor(private val settings: Settings) {

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
        updateTaskPaths()
    }

    private fun updateTaskPaths() {
        val startParameter = settings.gradle.startParameter
        mapTaskPaths(startParameter.taskNames, startParameter::setTaskNames)
        mapTaskPaths(startParameter.excludedTaskNames, startParameter::setExcludedTaskNames)
    }

    private inline fun mapTaskPaths(taskPaths: Iterable<String>, onChange: (List<String>) -> Unit) {
        val mappedTaskNames = taskPaths.map(::mapTaskPath)
        if (mappedTaskNames != taskPaths) {
            onChange(mappedTaskNames)
        }
    }

    private fun mapTaskPath(taskPath: String): String {
        val parts = taskPath.removePrefix(":").split(':')
        if (parts.size == 1) {
            return taskPath
        }
        var projectDefinition: ProjectDefinition? = rootProjectDefinition
        var mappedTaskName = ""
        for (i in 0..(parts.size - 2)) {
            projectDefinition = projectDefinition!!.children[parts[i]]
            if (projectDefinition == null) {
                return taskPath
            }
            mappedTaskName += ":${projectDefinition.descriptor.name}"
        }
        return "$mappedTaskName:${parts.last()}"
    }
}

class ProjectDefinition(val descriptor: ProjectDescriptor, private val path: String, private val settings: Settings) {

    internal val children = HashMap<String, ProjectDefinition>()

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
