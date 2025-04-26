# Gradle Structure Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.sgtsilvio.gradle.structure?color=brightgreen&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.sgtsilvio.gradle.structure)
[![GitHub](https://img.shields.io/github/license/sgtsilvio/gradle-structure?color=brightgreen&style=for-the-badge)](LICENSE)
[![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/sgtsilvio/gradle-structure/check.yml?branch=main&style=for-the-badge)](https://github.com/SgtSilvio/gradle-structure/actions/workflows/check.yml?query=branch%3Amain)

Gradle plugin to ease structuring and naming projects.

## How to Use

`settings.gradle.kts`

```kotlin
plugins {
    id("io.github.sgtsilvio.gradle.structure") version "0.2.0"
}

structure {
    group = "org.example"
    rootProject("example-app") {
        project("model")
        project("client") {
            project("ui")
        }
        project("server") {
            project("database")
            project("rest-api")
        }
    }
}
```

This results in the following fully qualified project names:
- `example-app`
- `example-app-model`
- `example-app-client`
- `example-app-client-ui`
- `example-app-server`
- `example-app-server-database`
- `example-app-server-rest-api`

Even though all project names are fully qualified, you can still use short project names in task paths.
The following list shows the command line path of the `build` task for each project:
- `./gradlew :build`
- `./gradlew :model:build`
- `./gradlew :client:build`
- `./gradlew :client:ui:build`
- `./gradlew :server:build`
- `./gradlew :server:database:build`
- `./gradlew :server:rest-api:build`

The following shows how the short path can be used for project dependencies in `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(structure.path(":server:database")))
}
```

## Comparison with Gradle's Default Multi-Project Configuration

This plugin was created due to issues with Gradle's default multi-project configuration.
Because the project names are coupled with the project paths, Gradle allows the following 2 options.
Neither of them enables to have fully qualified project names and short project paths.

### Option 1: Short Project Paths but Unqualified Project Names

`settings.gradle.kts`

```kotlin
rootProject.name = "example-app"
include("model")
include("client")
include("client:ui")
include("server")
include("server:database")
include("server:rest-api")
```

This results in the same short project paths, but unqualified project names:
- `example-app`
- `model`
- `client`
- `ui`
- `server`
- `database`
- `rest-api`

Why are these short project names an issue?
The project name is used for multiple things by Gradle itself - for example as convention for module coordinates, artifact names, capabilities - and by many plugins - for example by the Kotlin plugin for the module name.
Most of these usages require uniqueness, for which the short project names are not sufficient.
Think about how many projects would have a name like `database`, even as part of the same multi-project.

You might argue, that these conventions are configurable, but this is not the case for all places and this requires all plugins to provide this configurability.
Even if possible, a lot of customization in build scripts makes things very inconvenient to use and is against the "convention over configuration" paradigm.

[This core Gradle issue](https://github.com/gradle/gradle/issues/847) shows that unqualified project names are problematic.
This issue also can not easily be solved with custom configuration.

### Option 2: Fully Qualified Project Names but Long and Repetitive Project Paths

`settings.gradle.kts`

```kotlin
rootProject.name = "example-app"
include("example-app-model")
project(":example-app-model").projectDir = rootDir.resolve("model")
include("example-app-client")
project(":example-app-client").projectDir = rootDir.resolve("client")
include("example-app-client:example-app-client-ui")
project(":example-app-client:example-app-client-ui").projectDir = rootDir.resolve("client/ui")
include("example-app-server")
project(":example-app-server").projectDir = rootDir.resolve("server")
include("example-app-server:example-app-server-database")
project(":example-app-server:example-app-server-database").projectDir = rootDir.resolve("server/database")
include("example-app-server:example-app-server-rest-api")
project(":example-app-server:example-app-server-rest-api").projectDir = rootDir.resolve("server/rest-api")
```

This results in the same fully qualified project names, but long and repetitive project paths:
- `./gradlew :build`
- `./gradlew :example-app-model:build`
- `./gradlew :example-app-client:build`
- `./gradlew :example-app-client:example-app-client-ui:build`
- `./gradlew :example-app-server:build`
- `./gradlew :example-app-server:example-app-server-database:build`
- `./gradlew :example-app-server:example-app-server-rest-api:build`

What is the issue with long project paths?
The long and repetitive project paths are mostly inconvenient to use.
This inconvenience together with the non-standard verbose configuration inside the settings file very likely let's people not choose this option.
Furthermore, the Gradle documentation does not have any recommendation to use fully qualified names, all examples use short names.

### Conclusion

Ideally, this plugin should not be necessary.
Gradle's default convention should be to generate fully qualified project names, as this will avoid running into issues and is generally the right thing to do.
Still, the hierarchical project paths should not be repetitive.

Until Gradle improves this, this plugin tries to provide the best of both fully qualified project names and short project paths.
As Gradle's configuration in this area is limited, some inconveniences need to be accepted, for example a more verbose syntax for project dependencies `project(structure.path(":server:database"))` instead of `project(":server:database")`.
