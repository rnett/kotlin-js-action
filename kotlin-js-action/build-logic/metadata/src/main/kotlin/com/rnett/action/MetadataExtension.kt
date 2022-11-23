package com.rnett.action

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class MetadataExtension @Inject constructor(project: Project) {
    abstract val title: Property<String>

    val description: Provider<String> = project.provider { project.description }

}
