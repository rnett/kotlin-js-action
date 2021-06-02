plugins {
    kotlin("jvm") version "1.5.10"
    id("com.vanniktech.maven.publish") version "0.15.1"
    id("org.jetbrains.dokka") version "1.4.32"
    `java-gradle-plugin`
    `kotlin-dsl`
//    id("com.gradle.plugin-publish") version "0.11.0"

//    kotlin("js") version "1.4.30" apply false
}

group = "com.github.rnett.ktjs-github-action"
version = "1.2.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies{
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
}

kotlin{
    target{
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
    }
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
