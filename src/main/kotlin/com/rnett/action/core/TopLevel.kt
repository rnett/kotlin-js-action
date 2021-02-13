package com.rnett.action.core

import com.rnett.action.currentProcess
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Mask [this] in log output.
 */
public fun String.maskSecret(): Unit = maskSecret(this)

/**
 * Mask [secret] in log output.
 */
public fun maskSecret(secret: String): Unit = maskSecret(secret)


/**
 * Fail with [message], logging [message] as an error and killing the process.
 */
@Suppress("UNREACHABLE_CODE")
public fun fail(message: String): Nothing {
    log.fatal(message)
    currentProcess.exit(1)
    return error("")
}

/**
 * Fail with [exception], logging [exception] as an error and killing the process.
 */
@Suppress("UNREACHABLE_CODE")
public fun fail(exception: Throwable): Nothing {
    log.fatal(exception)
    currentProcess.exit(1)
    return error("")
}

/**
 * Runs [block], [fail]-ing if an exception is thrown and not caught within [block].
 */
public inline fun <R> runOrFail(block: () -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block()
    } catch (e: Throwable) {
        fail(e)
    }
}

/**
 * Runs [block], logging (as [log.error]) any exceptions that are not caught within [block].
 */
public inline fun <R> runOrLogException(block: () -> R): R? {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block()
    } catch (e: Throwable) {
        log.error(e)
        return null
    }
}