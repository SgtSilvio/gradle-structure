package io.github.sgtsilvio.gradle.structure

internal class ProjectPathMapping {
    val gradleToShort = HashMap<String, String>()
    val directoryToShort = HashMap<String, String>()
    val shortToFull = HashMap<String, String>()
}

internal fun String.resolveProjectPath(other: String) = when {
    other.startsWith(':') -> other
    this == ":" -> ":$other"
    else -> "$this:$other"
}
