package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import java.io.File
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

/**
 * @param addDependency whether to add the created task as a dependency of `browserProductionWebpack`
 */
fun Project.addWebpackGenTask(addDependency: Boolean = true): Task {
    val configTask = tasks.create("createGithubActionWebpackConfig"){
        group = "kotlin js github action"
        it.doLast {
            val directory = File("$projectDir/webpack.config.d/")
            if(!directory.exists())
                directory.mkdir()

            File("$directory/github.action.config.js")
                .writeText("config.target = 'node';")
        }
    }

    if(addDependency) {
        val webpackTask = tasks.findByName("browserProductionWebpack") ?: error("No browserProductionWebpack found")
        webpackTask.dependsOn(configTask)
    }

    return configTask
}

private fun Project.addWebpackCopyTask(outputFile: File){
    val webpackTask = tasks.getByName("browserProductionWebpack") as KotlinWebpack
    val copyDist = tasks.create("copyGithubActionDistributable", Copy::class.java){
        group = "kotlin js github action"
        it.from(webpackTask.outputFile)
        it.into(outputFile.parentFile)
        it.rename(webpackTask.outputFile.name, outputFile.name)
    }
    tasks.getByName("build").dependsOn(copyDist)
}

@OptIn(ExperimentalStdlibApi::class)
private inline fun <reified T : Any> Project.the(): T =
    convention.findByType(T::class.java)
        ?: convention.findPlugin(T::class.java)
        ?: convention.getByType(T::class.java)

fun KotlinJsTargetDsl.githubAction(
    outputFile: File = File("${project.projectDir}/dist/index.js"),
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