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

rootProject.name = "kotlin-js-action-parent"

include("kotlin-js-action", "serialization", "kotlin-js-action-plugin")
