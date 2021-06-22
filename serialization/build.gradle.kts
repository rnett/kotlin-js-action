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