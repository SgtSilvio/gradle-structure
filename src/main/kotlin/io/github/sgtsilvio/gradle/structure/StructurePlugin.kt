package io.github.sgtsilvio.gradle.structure

import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create

/**
 * @author Silvio Giebl
 */
@Suppress("unused")
class StructurePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val extension = settings.extensions.create("structure", StructureExtension::class, settings)
        settings.gradle.projectsLoaded {
            updateTaskPaths(startParameter, extension.rootProjectDefinition)
        }
    }

    private fun updateTaskPaths(startParameter: StartParameter, rootProjectDefinition: ProjectDefinition) {
        mapTaskPaths(startParameter.taskNames, rootProjectDefinition, startParameter::setTaskNames)
        mapTaskPaths(startParameter.excludedTaskNames, rootProjectDefinition, startParameter::setExcludedTaskNames)
    }

    private inline fun mapTaskPaths(
        taskPaths: Iterable<String>,
        rootProjectDefinition: ProjectDefinition,
        onChange: (List<String>) -> Unit,
    ) {
        val mappedTaskNames = taskPaths.map { mapTaskPath(it, rootProjectDefinition) }
        if (mappedTaskNames != taskPaths) {
            onChange(mappedTaskNames)
        }
    }

    private fun mapTaskPath(taskPath: String, rootProjectDefinition: ProjectDefinition): String {
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
