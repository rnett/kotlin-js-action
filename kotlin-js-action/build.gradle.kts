import java.net.URL

plugins {
    kotlin("js")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

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
    //TODO to complicated and not useful enough to be worth doing
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
                remoteUrl.set(URL("https://github.com/rnett/kotlin-js-action/blob/$sourceLinkBranch/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/"))
            }
        }
    }
}