package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

/**
 * Create a task that calls [generateAutoBuildWorkflow].
 *
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion the java version to use in the workflow (necessary for gradle).
 * @param gradleTasks the gradle command line.  Called like `./gradlew $gradleTasks`.
 * @param runner the github actions runner to use.
 * @param dir the directory to create `.github/workflows/auto_build_action.yml` in.  `rootDir` by default.
 */
fun Project.autoBuildWorkflowTask(
    add: String = "dist",
    message: String = "Commit new $add",
    gradleTasks: String = "build",
    javaVersion: String = "15",
    runner: String = "ubuntu-latest",
    dir: File = rootDir,
    configure: Task.() -> Unit = {}
) = tasks.register("generateAutoBuildWorkflow") {
    doLast {
        generateAutoBuildWorkflow(dir, add, message, gradleTasks, javaVersion, runner)
    }
    configure()
}

/**
 * Generate sa GitHub action workflow to build (with `gradlew assemble`) and commit `dist/` on each push, if the updates one wasn't build locally.
 *
 * Generates the workflow file in `$rootDir/.github/workflows/` by default.
 *
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion the java version to use in the workflow (necessary for gradle).
 * @param gradleTasks the gradle command line.  Called like `./gradlew $gradleTasks`.
 * @param runner the github actions runner to use.
 * @param dir the directory to create `.github/workflows/auto_build_action.yml` in.  `rootDir` by default.
 */
fun Project.generateAutoBuildWorkflow(
    add: String = "dist",
    message: String = "Commit new $add",
    gradleTasks: String = "build",
    javaVersion: String = "15",
    runner: String = "ubuntu-latest",
    dir: File = rootDir
) {
    generateAutoBuildWorkflow(dir, add, message, gradleTasks, javaVersion, runner)
}

/**
 * Generate a GitHub action workflow to build (with `gradlew assemble`) and commit `dist/` on each push, if the updates one wasn't build locally.
 *
 * @param dir the directory to create `.github/workflows/auto_build_action.yml` in.
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion the java version to use in the workflow (necessary for gradle).
 * @param gradleTasks the gradle command line.  Called like `./gradlew $gradleTasks`.
 * @param runner the github actions runner to use.
 */
fun generateAutoBuildWorkflow(
    dir: File,
    add: String = "dist",
    message: String = "Commit new $add",
    gradleTasks: String = "build",
    javaVersion: String = "15",
    runner: String = "ubuntu-latest"
) {
    val file = File("$dir/.github/workflows/auto_build_action.yml")
    file.parentFile.apply {
        if (!exists())
            mkdirs()
    }

    file.writeText(
        // language=yml
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
    runs-on: $runner

    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK
        uses: actions/setup-java@v1
        with:
          java-version: $javaVersion
          
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        
      - name: Build
        run: ./gradlew $gradleTasks
        
      - name: Commit dist
        uses: EndBug/add-and-commit@v7
        with:
          add: '$add'
          message: '$message'
                """.trimIndent()
    )
}