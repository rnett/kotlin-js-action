import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
}

description = "Utilities for writing Kotlin JS GitHub actions, including wrappers around @actions/toolkit"
ext["pomName"] = "Kotlin JS GitHub Action SDK"

val generateExternals = false

dependencies {
    testImplementation(kotlin("test-js"))

    api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    implementation(npm("@actions/core", "1.3.0", generateExternals))
    implementation(npm("@actions/exec", "1.0.4", generateExternals))
    implementation(npm("@actions/glob", "0.1.2", generateExternals))
    implementation(npm("@actions/io", "1.1.0", generateExternals))
    //TODO breaks dukat
//    implementation(npm("@actions/tool-cache", "1.6.1"))
    //TODO should do https://github.com/actions/toolkit/blob/30e0a77337213de5d4e158b05d1019c6615f69fd/packages/github/src/context.ts at least
    // don't need the actual lib for that
//    implementation(npm("@actions/github", "4.0.0", generateExternals))
    implementation(npm("@actions/artifact", "0.5.1", generateExternals))
    implementation(npm("@actions/cache", "1.0.7", generateExternals))
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
            runTask { nodeJs.nodeVersion = "12.20.2"}
        }
    }
    explicitApi()
    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
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