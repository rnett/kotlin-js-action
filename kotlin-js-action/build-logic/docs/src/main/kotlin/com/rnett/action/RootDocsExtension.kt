package com.rnett.action

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.Project
import org.jetbrains.dokka.Platform
import javax.inject.Inject

abstract class RootDocsExtension @Inject constructor(val project: Project) {
    abstract val readmeHeader: Property<String>

    val title: Provider<String> get() = project.extensions.getByType(MetadataExtension::class.java).title
}