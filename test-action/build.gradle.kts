import com.rnett.action.githubAction

plugins {
    kotlin("js") version "1.5.10"
    id("com.github.rnett.ktjs-github-action")
}

group = "com.github.rnett.ktjs-github-action.test"
version = "1.3.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.github.rnett.ktjs-github-action:kotlin-js-action")
    implementation("com.github.rnett.ktjs-github-action:serialization")
    implementation(kotlin("test"))
}

kotlin {
    js(IR) {
        githubAction(buildDir.resolve("dist/index.js"))
    }
}