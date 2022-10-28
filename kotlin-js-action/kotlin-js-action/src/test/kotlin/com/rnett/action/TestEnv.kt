package com.rnett.action

import kotlinx.js.get
import kotlin.reflect.KProperty

object TestEnv {
    private operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        val key = "TEST_ENV_${property.name}"
        return currentProcess.env[key] ?: error("Test environment variable $key not set")
    }

    val os by this
    val projectDirPath by this
    val tempDir by this
    val userHome by this
    val projectDir get() = Path(projectDirPath)
}