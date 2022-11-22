package com.rnett.action

import node.WritableStream
import node.buffer.Buffer
import node.process.Process
import node.stream.Duplex
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Convert a camelCase string to snake-case.
 */
public fun String.camelToSnakeCase(): String = replace(Regex("[^A-Z][A-Z]")) {
    it.value[0] + "-" + it.value[1].lowercaseChar()
}

/**
 * Convert a snake-case string to camelCase.
 */
public fun String.snakeToCamelCase(): String = replace(Regex("-[a-z]")) { it.value[1].uppercaseChar().toString() }


/**
 * Create a JavaScript object for the given interface.
 */
public inline fun <T : Any> JsObject(block: T.() -> Unit = {}): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    node.os.arch()
    val value = js("{}") as T
    value.block()
    return value
}

/**
 * Get the entries of a JS object, using `Object.entries`.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun jsEntries(jsObject: dynamic): Map<String, dynamic> = js("Object")
    .entries(jsObject)
    .unsafeCast<Array<Array<dynamic>>>()
    .map {
        it[0].unsafeCast<String>() to it[1]
    }.toMap()

/**
 * Get the current process.  Alias for [process] that doesn't have name conflicts.
 */
public val currentProcess: Process get() = node.process.process

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun WritableStream.writeLine(buffer: String): Boolean = write(buffer + OperatingSystem.lineSeparator)

/**
 * Write a line to [this], using the OS's line seperator.
 */
public fun Duplex.writeLine(buffer: String): Boolean = write(buffer + OperatingSystem.lineSeparator)

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