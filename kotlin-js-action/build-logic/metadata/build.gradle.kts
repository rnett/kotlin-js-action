plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("kjs-metadata") {
            id = "kjs-action.metadata"
            implementationClass = "com.rnett.action.MetadataPlugin"
        }
    }
}
