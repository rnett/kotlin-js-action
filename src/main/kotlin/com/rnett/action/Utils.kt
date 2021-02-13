package com.rnett.action

import NodeJS.Process
import NodeJS.WriteStream
import com.rnett.action.core.log
import kotlinx.coroutines.*
import process
import setInterval
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

public typealias ValProvider<T> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>>

public typealias LazyValProvider<T> = PropertyDelegateProvider<Any?, Lazy<T>>

public typealias VarProvider<T> = PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>>

public typealias AnyVarProperty<T> = ReadWriteProperty<Any?, T>

public inline fun <T : Any> JsObject(block: T.() -> Unit = {}): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    os.arch()
    val value = js("{}") as T
    value.block()
    return value
}

public inline fun runAction(block: () -> Unit){
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try{
        block()
    } catch (e: Throwable){
        log.fatal(e)
    }
}

public val currentProcess: Process get() = process

public fun WriteStream.writeLine(buffer: String): Boolean = write(buffer + os.EOL)
public fun process.global.NodeJS.WriteStream.writeLine(buffer: String): Boolean = write(buffer + os.EOL)