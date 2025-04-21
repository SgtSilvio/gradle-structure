package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Action
import org.gradle.api.IsolatedAction
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

open class StructureProjectAction internal constructor(
    private val projectPathMapping: ProjectPathMapping,
) : Action<Project> {
    override fun execute(project: Project) {
        project.extensions.create(EXTENSION_NAME, StructureProjectExtension::class, projectPathMapping)
    }
}

class StructureProjectIsolatedAction internal constructor(
    projectPathMapping: ProjectPathMapping,
) : StructureProjectAction(projectPathMapping), IsolatedAction<Project>