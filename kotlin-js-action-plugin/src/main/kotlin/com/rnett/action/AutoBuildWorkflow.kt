package com.rnett.action

import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.File


/**
 * Adds a task to generate a GitHub action workflow to build and commit `dist/` on each push, if the updates one wasn't build locally.
 *
 * Adds the task as a dependency of `wrapper` and `build`.
 *
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion The java version to use in the workflow (necessary for gradle).
 */
fun Project.useAutoBuildWorkflow(
    add: String = "dist",
    message: String = "Commit new $add",
    javaVersion: String = "15"
) {
    val genTask = tasks.create(Constants.generateWorkflowTaskName) {
        doLast {
            val file = File("$projectDir/.github/workflows/auto_build.yml")
            file.parentFile.apply {
                if (!exists())
                    mkdirs()
            }

            file.writeText(
                """
name: Auto-update dist

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    name: Update dist
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v1
        with:
          java-version: $javaVersion
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Build and Test
        run: ./gradlew build
      - name: Commit dist
        uses: EndBug/add-and-commit@v7
        with:
          add: '$add'
          message: '$message'
                """.trimIndent()
            )
        }
    }
    tasks["wrapper"].dependsOn(genTask)
    tasks["build"].dependsOn(genTask)
}