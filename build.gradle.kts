import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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

kotlin {
    jvmToolchain(21)
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        apiVersion = KotlinVersion.KOTLIN_2_0
        languageVersion = KotlinVersion.KOTLIN_2_0
    }
}

tasks.compileJava {
    javaCompiler = javaToolchains.compilerFor {
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
            compatibility {
                features {
                    configurationCache = true
                    isolatedProjects = true
                }
            }
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingKey").orNull
    val signingPassword = providers.gradleProperty("signingPassword").orNull
    useInMemoryPgpKeys(signingKey, signingPassword)
}
