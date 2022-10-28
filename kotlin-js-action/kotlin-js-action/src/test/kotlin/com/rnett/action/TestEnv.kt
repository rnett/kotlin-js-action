package com.rnett.action

import NodeJS.get
import kotlin.reflect.KProperty

object TestEnv {
    private operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        val key = "TEST_ENV_${property.name}"
        return currentProcess.env[key] ?: error("Test environment variable $key not set")
    }

    val os by this
    val testCwd by this
    val userHome by this
    val cwd get() = Path(testCwd)
}