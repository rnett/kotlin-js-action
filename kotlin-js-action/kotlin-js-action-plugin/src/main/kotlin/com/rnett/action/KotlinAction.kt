package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import java.io.File

/**
 * Add a task to create the custom webpack config necessary for packing GitHub actions.  Done automatically in [githubAction].
 */
fun Project.addWebpackGenTask(): TaskProvider<Task> = tasks.register(Constants.createWebpackTaskName) {
    group = Constants.taskGroup
    val directory = File("$projectDir/webpack.config.d/")
    val outputFile = File("$directory/github.action.config.js")
    doLast {
        if (!directory.exists())
            directory.mkdir()

        outputFile.writeText("config.target = 'node';")
    }
    outputs.file(outputFile)
        .withPropertyName("outputFile")
}

/**
 * Adds a JS target for GitHub actions (browser commonJs w/ node libraries) that run using `node12` and configures necessary tasks for packing.
 *
 * Running the production webpack task will generate the compiled GitHub task in [outputDir]/[outFileName], which by default is `dist/index.js`.
 */
fun KotlinJsTargetDsl.githubAction(
    outputDir: Directory = project.layout.projectDirectory.dir("dist"),
    outFileName: String = "index.js",
    nodeVersion: String = "16.18.0"
) {

    useCommonJs()

    val webpackGenTask = project.addWebpackGenTask()
    binaries.executable()

    browser {
        distribution {
            this.directory = outputDir.asFile
            this.name = outFileName
        }
        webpackTask {
            if (mode == org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION) {
                output.globalObject = "this" // NodeJS mode
                sourceMaps = false
                this.outputFileName = outFileName

                dependsOn(webpackGenTask)
            }
        }
    }

    project.rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
        project.rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = nodeVersion
    }
}