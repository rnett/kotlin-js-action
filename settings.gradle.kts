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
    id("com.github.rnett.kotlin-bootstrap") version "0.0.2-SNAPSHOT"
}

rootProject.name = "kotlin-js-action"

include("kotlin-js-action", "kotlin-js-action-plugin")
