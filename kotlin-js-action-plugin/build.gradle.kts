plugins {
    kotlin("jvm") version "1.4.30"
    id("com.vanniktech.maven.publish") version "0.14.0"
    id("org.jetbrains.dokka") version "1.4.20"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.11.0"

//    kotlin("js") version "1.4.30" apply false
}

group = "com.github.rnett.ktjs-github-action"
version = "0.1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies{
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
}

kotlin{
    target {

    }
}

gradlePlugin {
    plugins {
        create("kotlinJsGithubActionPlugin") {
            id = "com.github.rnett.ktjs-github-action"
            displayName = "Kotlin JS Github Action Gradle Plugin"
            description = "Kotlin JS Github Action Gradle Plugin"
            implementationClass = "com.rnett.action.GithubActionPlugin"
        }
    }
}
