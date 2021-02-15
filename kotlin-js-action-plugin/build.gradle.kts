plugins {
    kotlin("jvm") version "1.4.30"
    id("com.vanniktech.maven.publish") version "0.14.0"
    id("org.jetbrains.dokka") version "1.4.20"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.11.0"

//    kotlin("js") version "1.4.30" apply false
}

group = "com.github.rnett.ktjs-github-action"
version = "0.1.4-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies{
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
}

tasks.compileJava{
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.compileKotlin{
    kotlinOptions{
        jvmTarget = "1.8"
        useIR = true
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
