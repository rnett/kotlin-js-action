plugins {
    id("kjs-action.docs-root")
    alias(libs.plugins.kotlin.js) apply false
    alias(libs.plugins.publish) apply false
}

metadata {
    title.set("Kotlin/JS GitHub Actions SDK")
}

docs {
    readmeHeader.set("Kotlin JS GitHub Action SDK")
}