package io.github.sgtsilvio.gradle.structure

import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create
import org.gradle.util.GradleVersion
import java.io.File

/**
 * @author Silvio Giebl
 */
@Suppress("unused")
class StructurePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val extension = settings.extensions.create(EXTENSION_NAME, StructureExtension::class, settings)
        settings.gradle.settingsEvaluated {
            val projectPathMapping = ProjectPathMapping(extension.rootProjectDefinition)
            updateTaskPaths(startParameter, projectPathMapping, rootDir)
            if (GradleVersion.current() >= GradleVersion.version("8.8")) {
                gradle.lifecycle.beforeProject(StructureProjectIsolatedAction(projectPathMapping, extension.group))
            } else {
                gradle.beforeProject(StructureProjectAction(projectPathMapping, extension.group))
            }
        }
    }

    private fun updateTaskPaths(
        startParameter: StartParameter,
        projectPathMapping: ProjectPathMapping,
        rootDirectory: File,
    ) {
        val currentProjectShortPath = startParameter.currentDir.relativeToOrNull(rootDirectory)?.let { directory ->
            projectPathMapping.mapDirectoryToShort(directory.path)
        }
        mapTaskPaths(
            startParameter.taskNames,
            projectPathMapping,
            currentProjectShortPath,
            startParameter::setTaskNames,
        )
        mapTaskPaths(
            startParameter.excludedTaskNames,
            projectPathMapping,
            currentProjectShortPath,
            startParameter::setExcludedTaskNames,
        )
    }

    private inline fun mapTaskPaths(
        taskPaths: Iterable<String>,
        projectPathMapping: ProjectPathMapping,
        currentProjectShortPath: String?,
        onChange: (List<String>) -> Unit,
    ) {
        val mappedTaskNames = taskPaths.map { mapTaskPath(it, projectPathMapping, currentProjectShortPath) }
        if (mappedTaskNames != taskPaths) {
            onChange(mappedTaskNames)
        }
    }

    private fun mapTaskPath(
        taskPath: String,
        projectPathMapping: ProjectPathMapping,
        currentProjectShortPath: String?,
    ): String {
        val projectPathEndIndex = taskPath.lastIndexOf(':')
        if (projectPathEndIndex <= 0) {
            return taskPath
        }
        val projectPath = taskPath.substring(0, projectPathEndIndex)
        val projectFullPath = projectPathMapping.mapShortToFull(projectPath, currentProjectShortPath) ?: return taskPath
        val taskName = taskPath.substring(projectPathEndIndex + 1)
        return "$projectFullPath:$taskName"
    }
}

const val EXTENSION_NAME = "structure"
