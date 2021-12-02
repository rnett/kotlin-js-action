import com.rnett.future.testing.kotlinFutureTesting

enableFeaturePreview("VERSION_CATALOGS")

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

rootProject.name = "kotlin-js-action-parent"

include("kotlin-js-action", "serialization", "kotlin-js-action-plugin")
