package io.github.sgtsilvio.gradle.structure

internal class ProjectPathMapping {
    val gradlePathToShortPath = HashMap<String, String>()
    val shortPathToFullPath = HashMap<String, String>()
}

internal fun String.resolveProjectPath(other: String) = when {
    other.startsWith(':') -> other
    this == ":" -> ":$other"
    else -> "$this:$other"
}
