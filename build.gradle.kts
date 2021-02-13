import java.net.URL

plugins {
    kotlin("js") version "1.4.30"
    id("com.vanniktech.maven.publish") version "0.14.0"
    id("org.jetbrains.dokka") version "1.4.20"

}

group = "com.github.rnett.ktjs-github-action"
version = "0.1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

val generateExternals = false

dependencies {
    testImplementation(kotlin("test-js"))

    api("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation(npm("@actions/core", "1.2.6", generateExternals))
    implementation(npm("@actions/exec", "1.0.4", generateExternals))
    implementation(npm("@actions/glob", "0.1.1", generateExternals))
    implementation(npm("@actions/io", "1.0.2", generateExternals))
    //TODO breaks dukat
//    implementation(npm("@actions/tool-cache", "1.6.1"))
    //TODO to complicated and not useful enough to be worth doing
    implementation(npm("@actions/github", "4.0.0", generateExternals))
    implementation(npm("@actions/artifact", "0.5.0", generateExternals))
    implementation(npm("@actions/cache", "1.0.6", generateExternals))
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
        }
    }
    explicitApi()
    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
        }
    }
}

plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
    the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "12.20.2"
}

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
