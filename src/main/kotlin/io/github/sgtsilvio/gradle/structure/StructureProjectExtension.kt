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

    fun path(shortPath: String) =
        projectPathMapping.mapShortToFull(shortPath, projectPathMapping.mapGradleToShort(project.path))
            ?: throw IllegalArgumentException("'$shortPath' is not a project path")
}