package com.rnett.action

import NodeJS.Process
import NodeJS.WriteStream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import process as nodeProcess

/**
 * Convert a camelCase string to snake-case.
 */
public fun String.camelToSnakeCase(): String = replace(Regex("[^A-Z][A-Z]")) {
    it.value[0] + "-" + it.value[1].toLowerCase()
}

/**
 * Convert a snake-case string to camelCase.
 */
public fun String.snakeToCamelCase(): String = replace(Regex("-[a-z]")) { it.value[1].toUpperCase().toString() }


/**
 * Create a JavaScript object for the given interface.
 */
public inline fun <T : Any> JsObject(block: T.() -> Unit = {}): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    os.arch()
    val value = js("{}") as T
    value.block()
    return value
}

/**
 * Get the current process.  Alias for [process] that doesn't have name conflicts.
 */
public val currentProcess: Process get() = nodeProcess

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun WriteStream.writeLine(buffer: String): Boolean = write(buffer + os.EOL)

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun process.global.NodeJS.WriteStream.writeLine(buffer: String): Boolean = write(buffer + os.EOL)