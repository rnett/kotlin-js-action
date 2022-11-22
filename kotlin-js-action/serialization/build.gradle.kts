plugins {
    id("kjs-action.js-module")
    alias(libs.plugins.kotlinx.serialization)
}

description = "Support for Kotlinx serialization use with GitHub APIs"
metadata{
    title.set("Kotlin JS GitHub Action SDK Serialization support")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)

    api(libs.kotlinx.serialization.json)
    implementation(project(":kotlin-js-action"))
}
