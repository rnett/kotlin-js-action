plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `kotlin-dsl`
}

description = "A Gradle plugin to easily configure GitHub action packing"
ext["niceName"] = "Kotlin JS GitHub Action Gradle Plugin"

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))
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
