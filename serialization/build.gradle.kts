import java.net.URL

plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
    kotlin("plugin.serialization")
}

description = "Support for Kotlinx serialization use with GitHub APIs"
ext["pomName"] = "Kotlin JS GitHub Action SDK Serialization support"

dependencies {
    testImplementation(kotlin("test"))

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation(project(":kotlin-js-action"))
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
            testTask {
                useMocha()
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

tasks.dokkaHtml {
    moduleName.set("Kotlin JS GitHub Action SDK Serialization support")
    moduleVersion.set(version.toString())

    dokkaSourceSets {
        val main by getting {
            includes.from("packages.md", "module.md")

            platform.set(org.jetbrains.dokka.Platform.js)

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/rnett/kotlin-js-action/blob/$sourceLinkBranch/serialization/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}