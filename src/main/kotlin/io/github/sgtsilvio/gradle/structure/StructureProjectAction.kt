package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Action
import org.gradle.api.IsolatedAction
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create

open class StructureProjectAction internal constructor(
    private val projectPathMapping: ProjectPathMapping,
    private val group: Provider<String>,
) : Action<Project> {

    override fun execute(project: Project) {
        group.orNull?.let { project.group = it }
        project.extensions.create(EXTENSION_NAME, StructureProjectExtension::class, projectPathMapping)
    }
}

class StructureProjectIsolatedAction internal constructor(
    projectPathMapping: ProjectPathMapping,
    group: Provider<String>,
) : StructureProjectAction(projectPathMapping, group), IsolatedAction<Project>
