import com.rnett.future.testing.kotlinFutureTesting

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent { snapshotsOnly() }
        }
    }
}
plugins {
    id("com.github.rnett.kotlin-future-testing") version "0.0.10-SNAPSHOT"
}

kotlinFutureTesting {
    generateGithubWorkflows(force = true) { both() }
}

rootProject.name = "kotlin-js-action"

include("kotlin-js-action", "kotlin-js-action-plugin")
