plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies{
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.5.10")
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
