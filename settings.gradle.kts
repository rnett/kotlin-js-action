import com.rnett.bootstrap.kotlinBootstrap

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
    id("com.github.rnett.kotlin-bootstrap") version "0.0.5-SNAPSHOT"
}

kotlinBootstrap {
    generateGithubWorkflow()
}

rootProject.name = "kotlin-js-action"

include("kotlin-js-action", "kotlin-js-action-plugin")
