enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("..")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent { snapshotsOnly() }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create(defaultLibrariesExtensionName.get()) {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "test-action"
