package io.github.sgtsilvio.gradle.structure

import java.io.File

internal class ProjectPathMapping(rootProjectDefinition: ProjectDefinition) {
    val gradleToShort = HashMap<String, String>()
    val directoryToShort = HashMap<String, String>()
    private val shortToFull = HashMap<String, String>()

    init {
        gradleToShort[":"] = ":"
        directoryToShort[""] = ":"
        shortToFull[":"] = ":"
        fill(rootProjectDefinition, "", "", rootProjectDefinition.descriptor.projectDir)
    }

    private fun fill(
        parentProjectDefinition: ProjectDefinition,
        parentShortPath: String,
        parentFullPath: String,
        rootDirectory: File,
    ) {
        for ((pathName, projectDefinition) in parentProjectDefinition.children) {
            val shortPath = "$parentShortPath:$pathName"
            val fullPath = "$parentFullPath:${projectDefinition.descriptor.name}"
            gradleToShort[projectDefinition.descriptor.path] = shortPath
            directoryToShort[projectDefinition.descriptor.projectDir.toRelativeString(rootDirectory)] = shortPath
            shortToFull[shortPath] = fullPath
            fill(projectDefinition, shortPath, fullPath, rootDirectory)
        }
    }

    fun mapShortToFull(shortPath: String, currentProjectShortPath: String?): String? {
        val isAbsolute = shortPath.startsWith(':')
        val absoluteProjectPath = if (isAbsolute) shortPath else {
            (currentProjectShortPath ?: return null).resolveProjectPath(shortPath)
        }
        val fullPath = shortToFull[absoluteProjectPath] ?: return null
        return if (isAbsolute) fullPath else {
            fullPath.split(':').takeLast(shortPath.count { it == ':' } + 1).joinToString(":")
        }
    }
}

internal fun String.resolveProjectPath(other: String) = when {
    other.startsWith(':') -> other
    this == ":" -> ":$other"
    else -> "$this:$other"
}
