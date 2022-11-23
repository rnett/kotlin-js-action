plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.processResources {
    from(rootDir.parentFile.parentFile.resolve("version.txt"))
}

gradlePlugin {
    plugins {
        create("kjs-metadata") {
            id = "kjs-action.metadata"
            implementationClass = "com.rnett.action.MetadataPlugin"
        }
    }
}
