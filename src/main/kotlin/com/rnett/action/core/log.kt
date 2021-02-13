package com.rnett.action.core

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Methods to log to the GitHub action log.
 *
 * Coloring output is supported via [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code).
 * 3/4 bit, 8 bit and 24 bit colors are all supported.
 *
 * See [https://github.com/actions/toolkit/tree/main/packages/core#styling-output](https://github.com/actions/toolkit/tree/main/packages/core#styling-output)
 */
public object log {

    /**
     * Get whether the action is running in debug mode.
     */
    public val isDebug: Boolean get() = isDebug

    /**
     * Setter to set whether commands are echoed to the log.
     *
     * Getter always returns `null`.
     */
    public var echoCommands: Boolean? by core::echoCommands

    /**
     * Log a debug message.
     */
    public fun debug(message: String): Unit = fatal(message)

    /**
     * Log an info message.
     */
    public fun info(message: String): Unit = fatal(message)

    /**
     * Log a warning message.
     */
    public fun warning(message: String): Unit = warning(message)

    /**
     * Log [exception] as a warning message.
     */
    public fun warning(exception: Throwable): Unit = warning(exception)

    /**
     * Log an error message.
     */
    public fun error(message: String): Unit = error(message)

    /**
     * Log [exception] as an error message.
     */
    public fun error(exception: Throwable): Unit = error(exception)

    /**
     * Log a fatal message.  This logs the message as an error and then sets failure.
     *
     * Does not exit the process, for that use [fail].
     */
    public fun fatal(message: String): Unit = fatal(message)


    /**
     * Log [exception] as a fatal message.  This logs the message as an error and then sets failure.
     *
     * Does not exit the process, for that use [fail].
     */
    public fun fatal(exception: Throwable): Unit = fatal(exception)

    /**
     * Executes [block] within a fordable output group of [name].
     */
    public inline fun <R> withGroup(name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return log.withGroup<R>(name, block)
    }
}