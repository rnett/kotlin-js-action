plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(project(":metadata"))
    api(libs.build.dokka)
    api(libs.build.dokka.versioning)
}

gradlePlugin {
    plugins {
        create("kjs-docs-leaf") {
            id = "kjs-action.docs-leaf"
            implementationClass = "com.rnett.action.DocsLeafPlugin"
        }
        create("kjs-docs-root") {
            id = "kjs-action.docs-root"
            implementationClass = "com.rnett.action.DocsRootPlugin"
        }
    }
}
