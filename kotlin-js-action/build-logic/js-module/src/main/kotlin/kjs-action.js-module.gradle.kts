plugins {
    id("org.jetbrains.kotlin.js")
    id("kjs-action.common-module")
}

kotlin {
    js(IR) {
        useCommonJs()
        nodejs {
            binaries.library()
            testTask {
                useMocha {
                    timeout = "20s"
                }
            }
        }
    }
    explicitApi()
    sourceSets.configureEach {
        languageSettings {
            optIn("kotlin.contracts.ExperimentalContracts")
            optIn("kotlin.RequiresOptIn")
        }
    }
}
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        nodeVersion = "16.18.0"
    }
}

docs {
    platform.set(org.jetbrains.dokka.Platform.js)
}