package com.rnett.action

import org.gradle.api.Project
import java.io.File


/**
 * Generate sa GitHub action workflow to build (with `gradlew assemble`) and commit `dist/` on each push, if the updates one wasn't build locally.
 *
 * Generates the workflow file in `$rootDir/.github/workflows/`.
 *
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion The java version to use in the workflow (necessary for gradle).
 */
fun Project.generateAutoBuildWorkflow(
    add: String = "dist",
    message: String = "Commit new $add",
    javaVersion: String = "15"
) {
    com.rnett.action.generateAutoBuildWorkflow(rootDir, add, message, javaVersion)
}

/**
 * Generate a GitHub action workflow to build (with `gradlew assemble`) and commit `dist/` on each push, if the updates one wasn't build locally.
 *
 * @param dir the directory to create `.github/workflows/auto_build_action.yml` in.
 * @param add the arguments to `git add`.  `dist` by default.
 * @param message the commit message.
 * @param javaVersion The java version to use in the workflow (necessary for gradle).
 */
fun generateAutoBuildWorkflow(
    dir: File,
    add: String = "dist",
    message: String = "Commit new $add",
    javaVersion: String = "15"
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
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK
        uses: actions/setup-java@v1
        with:
          java-version: $javaVersion
          
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        
      - name: Build
        run: ./gradlew assemble
        
      - name: Commit dist
        uses: EndBug/add-and-commit@v7
        with:
          add: '$add'
          message: '$message'
                """.trimIndent()
    )
}