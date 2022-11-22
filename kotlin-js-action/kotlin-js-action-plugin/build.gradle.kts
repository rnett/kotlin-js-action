plugins {
    id("kjs-action.common-module")
    `kotlin-dsl`
}

description = "A Gradle plugin to easily configure GitHub action packing"
metadata {
    title.set("Kotlin JS GitHub Action Gradle Plugin")
}

kotlin {
    target {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
    }
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

docs {
    platform.set(org.jetbrains.dokka.Platform.jvm)
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        create("kotlinJsGithubActionPlugin") {
            id = "com.github.rnett.ktjs-github-action"
            displayName = "Kotlin JS GitHub Action Gradle Plugin"
            description = "Kotlin JS GitHub Action Gradle Plugin"
            implementationClass = "com.rnett.action.GithubActionPlugin"
        }
    }
}
