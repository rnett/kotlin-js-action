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
public fun maskSecret(secret: String): Unit = core.setSecret(secret)


/**
 * Fail with [message], logging [message] as an error and killing the process.
 */
@Suppress("UNREACHABLE_CODE")
public fun fail(message: String): Nothing {
    core.setFailed(message)
    currentProcess.exit(1)
    return error("")
}

/**
 * Fail with [exception], logging [exception] as an error and killing the process.
 */
@Suppress("UNREACHABLE_CODE")
public fun fail(exception: Throwable): Nothing {
    core.setFailed(exception)
    exception.printStackTrace()
    currentProcess.exit(1)
    return error("")
}

/**
 * Runs [block], [fail]-ing if an exception is thrown and not caught within [block].
 *
 * Runs [finally] in the try-catch's `finally` block.  If [flush] is `true`, prints a newline in the finally block.
 */
public inline fun <R> runOrFail(finally: () -> Unit = {}, flush: Boolean = true, block: () -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block()
    } catch (e: Throwable) {
        fail(e)
    } finally {
        finally()
        if(flush)
            println()
    }
}

//TODO convert to kotlin.Result post 1.5
/**
 * Runs [block], logging (as [log.error]) any exceptions that are not caught within [block].
 *
 * Runs [finally] in the try-catch's `finally` block.  If [flush] is `true`, prints a newline in the finally block.
 */
public inline fun <R> runOrLogException(finally: () -> Unit = {}, flush: Boolean = true, block: () -> R): R? {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block()
    } catch (e: Throwable) {
        log.error(e)
        return null
    } finally {
        finally()
        if(flush)
            println()
    }
}