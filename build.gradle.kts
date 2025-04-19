plugins {
    `kotlin-dsl`
    signing
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.defaults)
    alias(libs.plugins.metadata)
}

group = "io.github.sgtsilvio.gradle"

metadata {
    readableName = "Gradle Structure Plugin"
    description = "Gradle plugin to ease structuring and naming projects"
    license {
        apache2()
    }
    developers {
        register("SgtSilvio") {
            fullName = "Silvio Giebl"
        }
    }
    github {
        org = "SgtSilvio"
        issues()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("structure") {
            id = "$group.$name"
            implementationClass = "$group.$name.StructurePlugin"
            tags = listOf("project-structure", "multi-project", "subprojects", "project-naming")
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}
