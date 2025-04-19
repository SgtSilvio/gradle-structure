# Gradle Structure Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.sgtsilvio.gradle.structure?color=brightgreen&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.sgtsilvio.gradle.structure)
[![GitHub](https://img.shields.io/github/license/sgtsilvio/gradle-structure?color=brightgreen&style=for-the-badge)](LICENSE)
[![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/sgtsilvio/gradle-structure/check.yml?branch=main&style=for-the-badge)](https://github.com/SgtSilvio/gradle-structure/actions/workflows/check.yml?query=branch%3Amain)

Gradle plugin to ease structuring and naming projects.

## How to Use

`settings.gradle(.kts)`

```kotlin
plugins {
    id("io.github.sgtsilvio.gradle.structure") version "0.1.0"
}

structure {
    rootProject("example-application") {
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
