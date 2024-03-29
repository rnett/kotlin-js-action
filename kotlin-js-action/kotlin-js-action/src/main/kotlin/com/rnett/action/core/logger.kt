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
public object logger {

    /**
     * Get whether the action is running in debug mode.
     */
    public val isDebug: Boolean get() = core.isDebug

    /**
     * Setter to set whether commands are echoed to the log.
     *
     * Getter always returns `null`.
     */
    public var echoCommands: Boolean? by core::echoCommands

    /**
     * Log a debug message.
     */
    public fun debug(message: String): Unit = core.debug(message)

    /**
     * Log an info message.
     */
    public fun info(message: String): Unit = core.info(message)

    /**
     * Log a notice message.
     */
    public fun notice(message: String): Unit = core.notice(message)

    /**
     * Log [exception] as a notice message.
     */
    public fun notice(exception: Throwable): Unit = core.notice(exception)

    /**
     * Log a notice message.
     */
    public fun notice(message: String, annotationProperties: AnnotationProperties): Unit =
        core.notice(message, annotationProperties)

    /**
     * Log [exception] as a notice message.
     */
    public fun notice(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        core.notice(exception, annotationProperties)

    /**
     * Log a warning message.
     */
    public fun warning(message: String): Unit = core.warning(message)

    /**
     * Log [exception] as a warning message.
     */
    public fun warning(exception: Throwable): Unit = core.warning(exception)

    /**
     * Log a warning message.
     */
    public fun warning(message: String, annotationProperties: AnnotationProperties): Unit =
        core.warning(message, annotationProperties)

    /**
     * Log [exception] as a warning message.
     */
    public fun warning(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        core.warning(exception, annotationProperties)

    /**
     * Log an error message.
     */
    public fun error(message: String): Unit = core.error(message)

    /**
     * Log [exception] as an error message.
     */
    public fun error(exception: Throwable): Unit = core.error(exception)

    /**
     * Log an error message.
     */
    public fun error(message: String, annotationProperties: AnnotationProperties): Unit =
        core.error(message, annotationProperties)

    /**
     * Log [exception] as an error message.
     */
    public fun error(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        core.error(exception, annotationProperties)

    /**
     * Log a fatal message.  This logs the message as an error and then sets failure.
     *
     * Does not exit the process, for that use [fail].
     */
    public fun fatal(message: String): Unit = core.setFailed(message)


    /**
     * Log [exception] as a fatal message.  This logs the message as an error and then sets failure.
     *
     * Does not exit the process, for that use [fail].
     */
    public fun fatal(exception: Throwable): Unit = core.setFailed(exception)

    /**
     * Executes [block] within a fordable output group of [name].
     */
    public inline fun <R> withGroup(name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return core.withGroup(name, block)
    }
}