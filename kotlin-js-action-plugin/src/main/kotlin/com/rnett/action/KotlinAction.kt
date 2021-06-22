package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Add a task to create the custom webpack config necessary for packing GitHub actions.  Done by automatically in [githubAction].
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
 * Running the production webpack task will generate the compiled GitHub task in [outputFile], which by default is `dist/index.js`.
 */
fun KotlinJsTargetDsl.githubAction(
    outputFile: File = File("${project.projectDir}/dist/index.js")
) {

    useCommonJs()

    val webpackGenTask = project.addWebpackGenTask()
    binaries.executable()

    browser {
        webpackTask {
            if (mode == org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION) {
                output.globalObject = "this" // NodeJS mode
                sourceMaps = false

                dependsOn(webpackGenTask)

                val packedFile = this.outputFile
                outputs.file(outputFile)
                    .withPropertyName("actionOutput")

//                project.tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME) { dependsOn(this@webpackTask) }

                doLast {
                    outputFile.parentFile.mkdirs()
                    Files.copy(packedFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }

    var current: Project? = project
    while (current != null) {
        current.extensions.findByType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java)?.nodeVersion =
            "12.20.2"
        current = current.parent
    }
}