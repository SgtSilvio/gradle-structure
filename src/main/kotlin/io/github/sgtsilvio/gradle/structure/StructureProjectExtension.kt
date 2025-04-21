package io.github.sgtsilvio.gradle.structure

import org.gradle.api.NonExtensible
import org.gradle.api.Project
import javax.inject.Inject

/**
 * @author Silvio Giebl
 */
@NonExtensible
abstract class StructureProjectExtension @Inject internal constructor(
    private val projectPathMapping: ProjectPathMapping,
    private val project: Project,
) {

    fun path(shortPath: String): String {
        val absoluteShortPath = if (shortPath.startsWith(':')) {
            shortPath
        } else {
            val projectShortPath = projectPathMapping.gradlePathToShortPath[project.path]
                ?: throw IllegalStateException("$project was not defined by the structure plugin")
            "${if (projectShortPath == ":") "" else projectShortPath}:$shortPath"
        }
        return projectPathMapping.shortPathToFullPath[absoluteShortPath]
            ?: throw IllegalArgumentException("'$shortPath' is not a project path")
    }
}