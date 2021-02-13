package com.rnett.action.core

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public object log {

    public val isDebug: Boolean get() = core.isDebug

    public var echoCommands: Boolean? by core::echoCommands

    public fun debug(message: String): Unit = core.debug(message)

    public fun info(message: String): Unit = core.info(message)

    public fun warning(message: String): Unit = core.warning(message)

    public fun warning(exception: Throwable): Unit = core.warning(exception)

    public fun error(message: String): Unit = core.error(message)

    public fun error(exception: Throwable): Unit = core.error(exception)

    public fun fatal(message: String): Unit = core.setFailed(message)

    public fun fatal(exception: Throwable): Unit = core.setFailed(exception)

    public inline fun <R> withGroup(name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return core.withGroup(name, block)
    }
}