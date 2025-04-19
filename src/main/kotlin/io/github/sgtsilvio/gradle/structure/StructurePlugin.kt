package io.github.sgtsilvio.gradle.structure

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create

/**
 * @author Silvio Giebl
 */
@Suppress("unused")
class StructurePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.extensions.create("structure", StructureExtension::class, settings)
    }
}
