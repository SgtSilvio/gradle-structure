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
        val isAbsolute = shortPath.startsWith(':')
        val absoluteShortPath = if (isAbsolute) {
            shortPath
        } else {
            val currentProjectShortPath = projectPathMapping.gradleToShort[project.path]
                ?: throw IllegalStateException("$project was not defined by the structure plugin")
            currentProjectShortPath.resolveProjectPath(shortPath)
        }
        val fullPath = projectPathMapping.shortToFull[absoluteShortPath]
            ?: throw IllegalArgumentException("'$shortPath' is not a project path")
        return if (isAbsolute) {
            fullPath
        } else {
            fullPath.split(':').takeLast(shortPath.count { it == ':' } + 1).joinToString(":")
        }
    }
}