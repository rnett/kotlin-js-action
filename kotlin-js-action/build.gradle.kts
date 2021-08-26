import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
}

description = "Utilities for writing Kotlin JS GitHub actions, including wrappers around @actions/toolkit"
ext["niceName"] = "Kotlin JS GitHub Action SDK"

val generateExternals = false

private val latestVersionRegex = Regex("\"dist-tags\":\\{\"latest\":\"([^\"]+)\"")


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

val kotlinxNodeJSVersion: String by extra
val coroutinesVersion: String by extra

dependencies {
    testImplementation(kotlin("test"))

    api("org.jetbrains.kotlinx:kotlinx-nodejs:$kotlinxNodeJSVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation(latestNpm("@actions/core", "1.4.0"))
    implementation(latestNpm("@actions/exec", "1.1.0"))
    implementation(latestNpm("@actions/glob", "0.2.0"))
    implementation(latestNpm("@actions/io", "1.1.1"))
    //TODO breaks dukat
    implementation(latestNpm("@actions/tool-cache", "1.7.1", false))
    implementation(latestNpm("@actions/github", "5.0.0"))
    implementation(latestNpm("@actions/artifact", "0.5.2"))
    implementation(latestNpm("@actions/cache", "1.0.7"))
    implementation(latestNpm("@actions/http-client", "1.0.11"))
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
            testTask {
                useMocha {
                    timeout = "20s"
                }

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
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.RequiresOptIn")
        }
    }
}

val sourceLinkBranch: String by project

tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>() {
    dokkaSourceSets.configureEach {
        platform.set(org.jetbrains.dokka.Platform.js)
        externalDocumentationLink {
            url.set(URL("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/"))
        }
    }
}