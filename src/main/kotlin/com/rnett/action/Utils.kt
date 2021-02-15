package com.rnett.action

import NodeJS.Process
import NodeJS.WriteStream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import process as nodeProcess

public typealias ValProvider<T> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>>

public typealias LazyValProvider<T> = PropertyDelegateProvider<Any?, Lazy<T>>

public typealias VarProvider<T> = PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>>

public typealias AnyVarProperty<T> = ReadWriteProperty<Any?, T>

/**
 * Convert a camelCase string to snake-case.
 */
public fun String.camelToSnakeCase(): String = replace(Regex("[^A-Z][A-Z]")){
    it.value[0] + "-" + it.value[1].toLowerCase()
}

/**
 * Convert a snake-case string to camelCase.
 */
public fun String.snakeToCamelCase(): String = replace(Regex("-[a-z]")){ it.value[1].toUpperCase().toString() }

/**
 * Transform reads from a delegate
 */
public inline fun <D, T, R> ReadOnlyProperty<D, T>.transformRead(crossinline read: (T) -> R): ReadOnlyProperty<D, R> =
    ReadOnlyProperty { thisRef, prop -> read(this@transformRead.getValue(thisRef, prop)) }

/**
 * Transform reads from and writes to a delegate
 */
public inline fun <D, T, R> ReadWriteProperty<D, T>.transformReadWrite(
    crossinline read: (T) -> R,
    crossinline write: (R) -> T
): ReadWriteProperty<D, R> {
    return object : ReadWriteProperty<D, R> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: R) {
            this@transformReadWrite.setValue(thisRef, property, write(value))
        }

        override fun getValue(thisRef: D, property: KProperty<*>): R {
            return read(this@transformReadWrite.getValue(thisRef, property))
        }
    }
}

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