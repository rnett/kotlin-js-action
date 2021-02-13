package com.rnett.action

import NodeJS.Process
import NodeJS.WriteStream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import process as nodeProcess

public typealias ValProvider<T> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>>

public typealias LazyValProvider<T> = PropertyDelegateProvider<Any?, Lazy<T>>

public typealias VarProvider<T> = PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>>

public typealias AnyVarProperty<T> = ReadWriteProperty<Any?, T>

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