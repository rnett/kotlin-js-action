plugins {
    id("kjs-action.docs-root")
    alias(libs.plugins.kotlin.js) apply false
    alias(libs.plugins.publish) apply false
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

metadata {
    title.set("Kotlin/JS GitHub Actions SDK")
}

docs {
    readmeHeader.set("Kotlin JS GitHub Action SDK")
}