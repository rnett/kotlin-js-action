import com.rnett.action.githubAction

plugins {
    alias(libs.plugins.kotlin.js)
    id("com.github.rnett.ktjs-github-action")
}

group = "com.github.rnett.ktjs-github-action.test"
version = "1.3.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.rnett.ktjs-github-action:kotlin-js-action")
    implementation("com.github.rnett.ktjs-github-action:serialization")
    implementation(kotlin("test"))
}

kotlin {
    js(IR) {
        githubAction(layout.buildDirectory.dir("dist").get())
    }
}
