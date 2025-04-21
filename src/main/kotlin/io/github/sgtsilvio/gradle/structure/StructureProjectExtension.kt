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
    project: Project,
) {

    private val currentProjectShortPath = projectPathMapping.mapGradleToShort(project.path)

    fun path(shortPath: String) = projectPathMapping.mapShortToFull(shortPath, currentProjectShortPath)
        ?: throw IllegalArgumentException("'$shortPath' is not a project path")
}