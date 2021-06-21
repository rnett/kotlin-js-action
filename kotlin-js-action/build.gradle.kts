import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
}

description = "Utilities for writing Kotlin JS GitHub actions, including wrappers around @actions/toolkit"
ext["pomName"] = "Kotlin JS GitHub Action SDK"

val generateExternals = false

dependencies {
    testImplementation(kotlin("test"))

    api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    implementation(npm("@actions/core", "1.3.0", generateExternals))
    implementation(npm("@actions/exec", "1.0.4", generateExternals))
    implementation(npm("@actions/glob", "0.2.0", generateExternals))
    implementation(npm("@actions/io", "1.1.1", generateExternals))
    //TODO breaks dukat
//    implementation(npm("@actions/tool-cache", "1.6.1"))
    implementation(npm("@actions/github", "5.0.0", generateExternals))
    implementation(npm("@actions/artifact", "0.5.1", generateExternals))
    implementation(npm("@actions/cache", "1.0.7", generateExternals))
    implementation(npm("@actions/http-client", "1.0.11", generateExternals))
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
            testTask {
                useMocha()

                val dir = rootProject.file("testdir")
                doFirst {
                    dir.deleteRecursively()
                    dir.mkdirs()
                }
                doLast {
                    dir.deleteRecursively()
                }
            }
        }
    }
    explicitApi()
    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "12.20.2"

val sourceLinkBranch: String by project

tasks.dokkaHtml {
    moduleName.set("Kotlin JS GitHub Action SDK")
    moduleVersion.set(version.toString())

    dokkaSourceSets {
        val main by getting {
            includes.from("packages.md", "module.md")

            platform.set(org.jetbrains.dokka.Platform.js)

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/rnett/kotlin-js-action/blob/$sourceLinkBranch/kotlin-js-action/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/"))
            }
        }
    }
}