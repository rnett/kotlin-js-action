plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(project(":metadata"))
    implementation(project(":docs"))
    implementation(libs.build.publish)
}