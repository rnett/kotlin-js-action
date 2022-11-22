package com.rnett.action

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import java.net.URL

class DocsLeafPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        project.plugins.apply(MetadataPlugin::class.java)
        project.plugins.apply("org.jetbrains.dokka")
        val extension = project.extensions.create("docs", LeafDocsExtension::class)

        tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>() {
            moduleName.set(extension.title)
            moduleVersion.set(version.toString())

            dokkaSourceSets.configureEach {
                includes.from(listOf(file("module.md"), file("packages.md"), file("README.md")).filter { it.exists() })
                includeNonPublic.set(false)
                suppressObviousFunctions.set(true)
                suppressInheritedMembers.set(false)
                skipDeprecated.set(false)
                skipEmptyPackages.set(true)
                jdkVersion.set(8)

                val sourceSet = this.sourceSetID.sourceSetName

                sourceLink {
                    localDirectory.set(file("src/$sourceSet/kotlin"))

                    remoteUrl.set(project.providers.gradleProperty("sourceLinkBranch").map { sourceLinkBranch ->

                        val githubRoot = buildString {
                            append("https://github.com/rnett/kotlin-js-action/blob/")
                            append(sourceLinkBranch)
                            append("/kotlin-js-action/")

                            val dir = project.projectDir.relativeTo(rootProject.projectDir).path.trim('/')

                            append("/$dir")
                        }

                        java.net.URL("$githubRoot/src/$sourceSet/kotlin")
                    })
                    remoteLineSuffix.set("#L")
                }

                platform.set(extension.platform)

                externalDocumentationLink {
                    url.set(URL("https://kotlin.github.io/kotlinx.coroutines/"))
                }

                externalDocumentationLink {
                    url.set(URL("https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/"))
                }
            }
        }
    }
}