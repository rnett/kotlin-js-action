import java.net.URL

plugins {
    id(libs.plugins.kotlin.js.get().pluginId)
    id(libs.plugins.dokka.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

description = "Support for Kotlinx serialization use with GitHub APIs"
ext["niceName"] = "Kotlin JS GitHub Action SDK Serialization support"

val serializationVersion: String by extra

dependencies {
    testImplementation(kotlin("test"))

    api(libs.kotlinx.serialization.json)
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
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.RequiresOptIn")
        }
    }
}

val sourceLinkBranch: String by project

afterEvaluate {
    tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>() {
        dokkaSourceSets.configureEach {
            platform.set(org.jetbrains.dokka.Platform.js)
            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/"))
            }
        }
    }
}