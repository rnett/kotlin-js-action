package com.rnett.action

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.Project
import org.jetbrains.dokka.Platform
import javax.inject.Inject

abstract class LeafDocsExtension @Inject constructor(val project: Project) {
    abstract val platform: Property<Platform>

    val title: Provider<String> get() = project.extensions.getByType(MetadataExtension::class.java).title
}