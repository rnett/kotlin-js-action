import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
}

description = "Utilities for writing Kotlin JS GitHub actions, including wrappers around @actions/toolkit"
ext["pomName"] = "Kotlin JS GitHub Action SDK"

val generateExternals = false

private val latestVersionRegex = Regex("\"dist-tags\":\\{\"latest\":\"([^\"]+)\"\\}")


fun DependencyHandlerScope.latestNpm(
    name: String,
    version: String,
    generate: Boolean = generateExternals
): NpmDependency {
    val url = "https://registry.npmjs.org/$name/"
    val latest = latestVersionRegex.find(URL(url).readText())?.groupValues?.get(1) ?: error("Version not found in $url")

    if (latest != version) {
        val message = "Using old version of npm library $name: Using $version, but latest was $latest"
        if ((findProperty("enforceLatest")?.toString()?.toLowerCase() ?: "false") != "false")
            error(message)
        logger.warn(message)
    }
    return npm(name, version, generate)
}

dependencies {
    testImplementation(kotlin("test"))

    api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    implementation(latestNpm("@actions/core", "1.4.0"))
    implementation(latestNpm("@actions/exec", "1.1.0"))
    implementation(latestNpm("@actions/glob", "0.2.0"))
    implementation(latestNpm("@actions/io", "1.1.1"))
    //TODO breaks dukat
//    implementation(npm("@actions/tool-cache", "1.6.1"))
    implementation(latestNpm("@actions/github", "5.0.0"))
    implementation(latestNpm("@actions/artifact", "0.5.1"))
    implementation(latestNpm("@actions/cache", "1.0.7"))
    implementation(latestNpm("@actions/http-client", "1.0.11"))
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

val sourceLinkBranch: String by project

tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaTask>() {
    val dokkaSourceSets = when (this) {
        is org.jetbrains.dokka.gradle.DokkaTask -> dokkaSourceSets
        is org.jetbrains.dokka.gradle.DokkaTaskPartial -> dokkaSourceSets
        else -> return@withType
    }
    dokkaSourceSets.configureEach {
        platform.set(org.jetbrains.dokka.Platform.js)

        externalDocumentationLink {
            url.set(URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/"))
        }
    }
}