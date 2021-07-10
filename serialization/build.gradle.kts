import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
    kotlin("plugin.serialization")
}

description = "Support for Kotlinx serialization use with GitHub APIs"
ext["niceName"] = "Kotlin JS GitHub Action SDK Serialization support"

val serializationVersion: String by extra

dependencies {
    testImplementation(kotlin("test"))

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation(project(":kotlin-js-action"))
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

afterEvaluate {
    tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaTask>() {
        val dokkaSourceSets = when (this) {
            is org.jetbrains.dokka.gradle.DokkaTask -> dokkaSourceSets
            is org.jetbrains.dokka.gradle.DokkaTaskPartial -> dokkaSourceSets
            else -> return@withType
        }
        dokkaSourceSets.configureEach {
            platform.set(org.jetbrains.dokka.Platform.js)
            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/"))
            }
        }
    }
}