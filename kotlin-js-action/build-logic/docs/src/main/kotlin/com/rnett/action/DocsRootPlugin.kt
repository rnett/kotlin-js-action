package com.rnett.action

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

class DocsRootPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        project.plugins.apply(MetadataPlugin::class.java)
        project.plugins.apply("org.jetbrains.dokka")

        val extension = project.extensions.create("docs", RootDocsExtension::class.java)

        val oldVersionsDir = providers.gradleProperty("oldVersionsDir")

        tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
            this.fileLayout.set(org.jetbrains.dokka.gradle.DokkaMultiModuleFileLayout.CompactInParent)
            this.includes.from("DOCS.md")
            this.moduleName.set(extension.title)
            this.moduleVersion.set(version.toString())
            if (oldVersionsDir.isPresent && "snapshot" !in project.version.toString().toLowerCase()) {
                val resolved = rootDir.resolve(oldVersionsDir.get())
                println("Using older versions from $resolved")
                pluginConfiguration<org.jetbrains.dokka.versioning.VersioningPlugin, org.jetbrains.dokka.versioning.VersioningConfiguration> {
                    version = project.version.toString()
                    olderVersionsDir = resolved
                }
            }
        }

        tasks.create<Copy>("generateReadme") {
            from(rootDir.resolve("../README.md"))
            into(buildDir.resolve("readme"))
            filter {
                val header = extension.readmeHeader.get()
                it.replace(
                    "# $header",
                    "# [$header](https://github.com/rnett/kotlin-js-action)"
                )
            }
        }
    }
}