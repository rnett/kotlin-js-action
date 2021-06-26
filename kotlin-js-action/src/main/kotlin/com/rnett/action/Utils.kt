package com.rnett.action

import Buffer
import NodeJS.Process
import NodeJS.WritableStream
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import stream.internal
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
 * Get the entries of a JS object, using `Object.entries`.
 */
public inline fun jsEntries(jsObject: dynamic): Map<String, dynamic> = js("Object")
    .entries(jsObject)
    .unsafeCast<Array<Array<dynamic>>>()
    .map {
        it[0].unsafeCast<String>() to it[1]
    }.toMap()

/**
 * Get the current process.  Alias for [process] that doesn't have name conflicts.
 */
public val currentProcess: Process get() = nodeProcess

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun WritableStream.writeLine(buffer: String): Boolean = write(buffer + os.EOL)

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun internal.Duplex.writeLine(buffer: String): Boolean = write(buffer + os.EOL)

/**
 * Non-copying conversion to a Kotlin [ByteArray].
 */
public fun ArrayBuffer.asByteArray(byteOffset: Int = 0, length: Int = this.byteLength): ByteArray =
    Int8Array(this, byteOffset, length).asByteArray()

/**
 * Non-copying conversion to a Kotlin [ByteArray].
 */
public fun Int8Array.asByteArray(): ByteArray = this.unsafeCast<ByteArray>()

/**
 * Non-copying conversion to a Kotlin [ByteArray].
 */
public fun Uint8Array.asByteArray(): ByteArray = Int8Array(buffer, byteOffset, length).asByteArray()

/**
 * Non-copying conversion to a [Int8Array].
 */
public fun ByteArray.asInt8Array(): Int8Array = this.unsafeCast<Int8Array>()

/**
 * Non-copying conversion to a [Buffer].
 */
public fun ByteArray.asBuffer(): Buffer = this.asInt8Array().let {
    Buffer(Uint8Array(it.buffer, it.byteOffset, it.length))
}