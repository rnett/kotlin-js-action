import org.apache.commons.lang3.SystemUtils
import org.jetbrains.kotlin.cli.common.toBooleanLenient
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency
import java.net.URL

plugins {
    alias(libs.plugins.kotlin.js)
    id("kjs-action.js-module")
}

description = "Utilities for writing Kotlin JS GitHub actions, including wrappers around @actions/toolkit"
metadata {
    title.set("Kotlin JS GitHub Action SDK")
}

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

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.wrappers.node)

    implementation(latestNpm("@actions/core", "1.10.0"))
    implementation(latestNpm("@actions/exec", "1.1.1"))
    implementation(latestNpm("@actions/glob", "0.3.0"))
    implementation(latestNpm("@actions/io", "1.1.2"))
    //TODO breaks dukat
    implementation(latestNpm("@actions/tool-cache", "2.0.1", false))
    implementation(latestNpm("@actions/github", "5.1.1"))
    implementation(latestNpm("@actions/artifact", "1.1.0"))
    implementation(latestNpm("@actions/cache", "3.0.6"))
    implementation(latestNpm("@actions/http-client", "2.0.1"))
}



kotlin {
    js(IR) {
        nodejs {
            testTask {
                val dir = layout.buildDirectory.dir("testdir").get().asFile
                doFirst {
                    dir.deleteRecursively()
                    dir.mkdirs()
                }
                doLast {
                    dir.deleteRecursively()
                }
                environment("TEST_ENV_tempDir", dir.absolutePath)

                if (System.getenv("CI").toBooleanLenient() != true) {
                    environment("TEST_ENV_projectDirPath", rootProject.layout.projectDirectory.asFile.parentFile.absolutePath)
                    environment(
                        "TEST_ENV_os", when {
                            SystemUtils.IS_OS_MAC -> "mac"
                            SystemUtils.IS_OS_LINUX -> "linux"
                            SystemUtils.IS_OS_WINDOWS -> "windows"
                            else -> error("Unsupported operating system")
                        }
                    )
                    environment(
                        "TEST_ENV_userHome",
                        File(System.getProperty("user.home")).canonicalFile.absolutePath
                    )
                }
            }
        }
    }
}
