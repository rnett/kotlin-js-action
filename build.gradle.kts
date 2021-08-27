buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:versioning-plugin:1.5.0")
    }
}
plugins {
    kotlin("js") version "1.5.30" apply false
    kotlin("jvm") version "1.5.30" apply false
    id("com.vanniktech.maven.publish") version "0.17.0" apply false
    id("org.jetbrains.dokka") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.30" apply false
}

allprojects {
    group = "com.github.rnett.ktjs-github-action"
    version = "1.4.4-SNAPSHOT"

    val serializationVersion by extra("1.2.2")
    val kotlinxNodeJSVersion by extra("0.0.7")
    val coroutinesVersion by extra("1.5.1")

    repositories {
        mavenCentral()
        jcenter()
    }
}

val sourceLinkBranch: String by project

val oldVersionsDir: String? by project

subprojects {
    afterEvaluate {
        val project = this

        apply(plugin = "org.jetbrains.dokka")
        apply(plugin = "org.gradle.maven-publish")
        apply(plugin = "com.vanniktech.maven.publish")

        extensions.getByType<com.vanniktech.maven.publish.MavenPublishBaseExtension>().apply {
            if (!version.toString().toLowerCase().endsWith("snapshot")) {
                val stagingProfileId = project.findProperty("sonatypeRepositoryId")?.toString()
                publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.DEFAULT, stagingProfileId)
            }

            pom {
                name.set(project.ext["niceName"].toString())
                description.set(project.description)
                inceptionYear.set("2021")
                url.set("https://github.com/rnett/kotlin-js-action/")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/rnett/kotlin-js-action.git")
                    connection.set("scm:git:git://github.com/rnett/kotlin-js-action.git")
                    developerConnection.set("scm:git:ssh://git@github.com/rnett/kotlin-js-action.git")
                }

                developers {
                    developer {
                        id.set("rnett")
                        name.set("Ryan Nett")
                        url.set("https://github.com/rnett/")
                    }
                }
            }
        }
        tasks.withType(AbstractTestTask::class.java).configureEach {
            testLogging {
                showExceptions = true   // It is true by default. Set it just for explicitness.
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }

        tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>() {
            moduleName.set(project.ext["niceName"].toString())
            moduleVersion.set(version.toString())

            dokkaSourceSets.configureEach {
                includes.from(listOf(file("module.md"), file("packages.md"), file("README.md")).filter { it.exists() })
                includeNonPublic.set(false)
                suppressObviousFunctions.set(true)
                suppressInheritedMembers.set(false)
                skipDeprecated.set(false)
                skipEmptyPackages.set(true)
                jdkVersion.set(8)

                val sourceSet = this.sourceSetID.sourceSetName

                sourceLink {
                    localDirectory.set(file("src/$sourceSet/kotlin"))

                    val githubRoot = buildString {
                        append("https://github.com/rnett/kotlin-js-action/blob/")
                        append(sourceLinkBranch)

                        val dir = project.projectDir.relativeTo(rootProject.projectDir).path.trim('/')

                        append("/$dir")
                    }

                    remoteUrl.set(java.net.URL("$githubRoot/src/$sourceSet/kotlin"))
                    remoteLineSuffix.set("#L")
                }
            }
        }
    }
}

allprojects {
    convention.findByType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>()?.nodeVersion = "12.20.2"
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    this.fileLayout.set(org.jetbrains.dokka.gradle.DokkaMultiModuleFileLayout.CompactInParent)
    this.includes.from("DOCS.md")
    this.moduleName.set("Kotlin/JS GitHub Actions SDK")
    this.moduleVersion.set(version.toString())
    if (oldVersionsDir != null && "snapshot" !in project.version.toString().toLowerCase()) {
        val resolved = rootDir.resolve(oldVersionsDir!!)
        println("Using older versions from $resolved")
        pluginConfiguration<org.jetbrains.dokka.versioning.VersioningPlugin, org.jetbrains.dokka.versioning.VersioningConfiguration> {
            version = project.version.toString()
            olderVersionsDir = resolved
        }
    }
}

val header = "Kotlin JS GitHub Action SDK"

tasks.create<Copy>("generateReadme") {
    from("README.md")
    into(buildDir)
    filter {
        it.replace(
            "# $header",
            "# [$header](https://github.com/rnett/kotlin-js-action)"
        )
    }
}