package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import java.io.File
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

/**
 * Add a task to create the custom webpack config necessary for acking GitHub actions.  Done by automatically in [githubAction]
 * @param addDependency whether to add the created task as a dependency of `browserProductionWebpack`.
 */
fun Project.addWebpackGenTask(addDependency: Boolean = true): Task {
    val configTask = tasks.create(Constants.createWebpackTaskName){
        group = Constants.taskGroup
        doLast {
            val directory = File("$projectDir/webpack.config.d/")
            if(!directory.exists())
                directory.mkdir()

            File("$directory/github.action.config.js")
                .writeText("config.target = 'node';")
        }
    }

    if(addDependency) {
        val webpackTask = tasks.findByName(Constants.jsProductionWebpackTask) ?: error("No ${Constants.jsProductionWebpackTask} found")
        webpackTask.dependsOn(configTask)
    }

    return configTask
}

private fun Project.addWebpackCopyTask(outputFile: File){
    val webpackTask = tasks.getByName(Constants.jsProductionWebpackTask) as KotlinWebpack
    val copyDist = tasks.create(Constants.copyDistTaskName, Copy::class.java){
        group = Constants.taskGroup
        from(webpackTask.outputFile)
        into(outputFile.parentFile)
        rename(webpackTask.outputFile.name, outputFile.name)
    }
    tasks.getByName("build").dependsOn(copyDist)
}

@OptIn(ExperimentalStdlibApi::class)
private inline fun <reified T : Any> Project.the(): T =
    convention.findByType(T::class.java)
        ?: convention.findPlugin(T::class.java)
        ?: convention.getByType(T::class.java)

/**
 * Adds a JS target for GitHub actions (browser commonJs w/ node libraries) and configures necessary tasks for packing.
 *
 * Running `build` will generate the compiled GitHub task in [outputFile], which by default is `dist/index.js`.
 */
fun KotlinJsTargetDsl.githubAction(
    outputFile: File = File("${project.projectDir}/dist/index.js")
) {

    useCommonJs()
    browser {
        webpackTask {
            output.globalObject = "this" // NodeJS mode
            sourceMaps = false
            mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
        }
    }
    binaries.executable()

    project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "12.20.2"
    project.addWebpackCopyTask(outputFile)
    project.addWebpackGenTask()
}