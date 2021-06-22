plugins {
    kotlin("js") version "1.5.10" apply false
    kotlin("jvm") version "1.5.10" apply false
    id("com.vanniktech.maven.publish") version "0.15.1" apply false
    id("org.jetbrains.dokka") version "1.4.32"
    kotlin("plugin.serialization") version "1.5.10" apply false
}

allprojects {
    group = "com.github.rnett.ktjs-github-action"
    version = "1.3.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
    }
}

val sourceLinkBranch: String by project

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
                name.set(project.ext["pomName"].toString())
                description.set(project.description)
                inceptionYear.set("2021")
                url.set("https://github.com/rnett/github-actions-gradle-cache/")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/rnett/github-actions-gradle-cache.git")
                    connection.set("scm:git:git://github.com/rnett/github-actions-gradle-cache.git")
                    developerConnection.set("scm:git:ssh://git@github.com/rnett/github-actions-gradle-cache.git")
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

        tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaTask>() {

            val (moduleName, moduleVersion, dokkaSourceSets) = when (this) {
                is org.jetbrains.dokka.gradle.DokkaTask -> Triple(moduleName, moduleVersion, dokkaSourceSets)
                is org.jetbrains.dokka.gradle.DokkaTaskPartial -> Triple(moduleName, moduleVersion, dokkaSourceSets)
                else -> return@withType
            }

            moduleName.set(project.ext["pomName"].toString())
            moduleVersion.set(version.toString())

            dokkaSourceSets.configureEach {
                includes.from(listOf(file("module.md"), file("packages.md"), file("README.md")).filter { it.exists() })
                includeNonPublic.set(false)
                suppressObviousFunctions.set(true)
                suppressInheritedMembers.set(true)
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
    this.moduleName.set("Kotlin/JS Github Actions SDK")
    this.moduleVersion.set(version.toString())
}