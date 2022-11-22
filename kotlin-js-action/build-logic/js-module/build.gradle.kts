plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(project(":common-module"))
    api(libs.build.kotlin.js)
}
