package io.github.sgtsilvio.gradle.structure

import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create
import java.io.File

/**
 * @author Silvio Giebl
 */
@Suppress("unused")
class StructurePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val extension = settings.extensions.create(EXTENSION_NAME, StructureExtension::class, settings)
        val rootProjectDefinition = extension.rootProjectDefinition
        settings.gradle.settingsEvaluated {
            val projectPathMapping = ProjectPathMapping(rootProjectDefinition)
            updateTaskPaths(startParameter, projectPathMapping, rootDir)
            gradle.beforeProject {
//            gradle.lifecycle.beforeProject {
                extensions.create(EXTENSION_NAME, StructureProjectExtension::class, projectPathMapping)
            }
        }
    }

    private fun updateTaskPaths(
        startParameter: StartParameter,
        projectPathMapping: ProjectPathMapping,
        rootDirectory: File,
    ) {
        val currentProjectShortPath = startParameter.currentDir.relativeToOrNull(rootDirectory)?.let { directoryPath ->
            projectPathMapping.directoryToShort[directoryPath.path]
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
        val isAbsolute = projectPath.startsWith(':')
        val absoluteProjectPath = when {
            isAbsolute -> projectPath
            currentProjectShortPath == null -> return taskPath
            else -> currentProjectShortPath.resolveProjectPath(projectPath)
        }
        val projectFullPath = projectPathMapping.shortToFull[absoluteProjectPath] ?: return taskPath
        val projectRelativeFullPath = when {
            isAbsolute -> projectFullPath
            else -> projectFullPath.split(':').takeLast(projectPath.count { it == ':' } + 1).joinToString(":")
        }
        val taskName = taskPath.substring(projectPathEndIndex + 1)
        return "$projectRelativeFullPath:$taskName"
    }
}

const val EXTENSION_NAME = "structure"
