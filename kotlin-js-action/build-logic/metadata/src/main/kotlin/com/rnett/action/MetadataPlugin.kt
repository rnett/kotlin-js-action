package com.rnett.action

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class MetadataPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val extension = project.extensions.create("metadata", MetadataExtension::class)
        group = "com.github.rnett.ktjs-github-action"
        version = "1.6.0-SNAPSHOT"
    }
}