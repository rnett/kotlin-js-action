package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetContainerDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import java.io.File

/**
 * Add a task to create the custom webpack config necessary for packing GitHub actions.  Done by automatically in [githubAction]
 * Adds it as a dependency of `compileKotlinJs` (always) and `browserProductionWebpack` (if [addDependency] is set).
 *
 * @param addDependency whether to add the created task as a dependency of `browserProductionWebpack`.
 */
fun Project.addWebpackGenTask(
    webpackTask: KotlinWebpack,
    addDependency: Boolean = true,
): TaskProvider<Task> {
    val configTask = tasks.register(Constants.createWebpackTaskName) {
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

    if (addDependency) {
        webpackTask.dependsOn(configTask)
    }

    return configTask
}

private fun Project.addWebpackCopyTask(webpackTask: KotlinWebpack, outputFile: File) {
    val copyDist = tasks.register(Constants.copyDistTaskName, Copy::class.java) {
        group = Constants.taskGroup
        from(webpackTask.outputFile)
        into(outputFile.parentFile)
        rename(webpackTask.outputFile.name, outputFile.name)
    }
    tasks.named("build"){ dependsOn(copyDist) }
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
    val webpackTaskName: String
    browser {
        webpackTask {
            output.globalObject = "this" // NodeJS mode
            sourceMaps = false
            mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION

            project.addWebpackCopyTask(this, outputFile)
            project.addWebpackGenTask(this)
        }
    }
    binaries.executable()

    this as KotlinJsSubTargetContainerDsl


    project.extensions
    var current: Project? = project
    while(current != null) {
        current.extensions.findByType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java)?.nodeVersion = "12.20.2"
        current = current.parent
    }
}