package com.rnett.action

import Buffer
import NodeJS.Process
import NodeJS.ReadableStream
import NodeJS.WritableStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import stream.TransformCallback
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
 * Convert a flow to a stream by writing the flow's elements to an [internal.PassThrough].
 *
 * Collects the flow in [GlobalScope] by default, eventually [scope] will become a context receiver.
 *
 * TODO make [scope] a context receiver.
 */
public suspend fun Flow<String>.toStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.PassThrough()
    launch {
        this@toStream.onCompletion { stream.end() }
            .collect {
                stream.write(it)
            }
    }
    stream
}

/**
 * Convert a flow to a stream by writing the flow's elements to an [internal.PassThrough].
 *
 * Collects the flow in [GlobalScope] by default, eventually [scope] will become a context receiver.
 *
 * TODO make [scope] a context receiver.
 */
public suspend fun Flow<Buffer>.toStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.PassThrough()
    launch {
        this@toStream.onCompletion { stream.end() }
            .collect {
                stream.write(it)
            }
    }
    stream
}

/**
 * Convert a flow to an object mode stream by writing the flow's elements to an [internal.PassThrough].
 *
 * Collects the flow in [GlobalScope] by default, eventually [scope] will become a context receiver.
 *
 * TODO make [scope] a context receiver.
 */
public suspend fun Flow<Any>.toObjectStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.Transform(object : internal.TransformOptions {
        init {
            this.readableObjectMode = true
            this.writableObjectMode = true
        }

        override val transform: ((chunk: Any, encoding: String, callback: TransformCallback) -> Unit) =
            { chunk: Any, encoding: String, callback: TransformCallback ->
                callback(null, chunk)
            }
    })
    launch {
        this@toObjectStream.onCompletion { stream.end() }
            .collect {
                stream.write(it)
            }
    }
    stream
}

@PublishedApi
@ExperimentalCoroutinesApi
internal suspend inline fun <reified T : Any> ProducerScope<T>.flowHelper(stream: ReadableStream) {
    stream.pipe(internal.Writable(object : internal.WritableOptions {
        init {
            this.objectMode = true
        }

        private fun writeImpl(chunk: Any, callback: (Error?) -> Unit) {
            if (chunk !is T) {
                val exception =
                    ClassCastException("Can't cast value to class ${T::class}: ${chunk::class} with value $chunk")
                callback(Error("Wrong element type", exception))
                channel.close(exception)
                return
            }
            this@flowHelper.launch {
                try {
                    channel.send(chunk)
                } catch (e: Throwable) {
                    if (e is CancellationException) {
                        throw e
                    }
                    callback(Error("Error"))
                    return@launch
                }
                callback(null)
            }
        }

        override val write: ((chunk: Any, encoding: String, callback: (error: Error?) -> Unit) -> Unit) =
            { chunk, encoding, callback ->
                writeImpl(chunk, callback)
            }

        override val destroy: ((error: Error?, callback: (error: Error?) -> Unit) -> Unit) = { error, callback ->
            channel.close(error)
            callback(null)
        }
    }))
    awaitClose()
}


/**
 * Convert a NodeJS stream to a flow.  The resulting flow can be consumed multiple times.
 *
 * The receiver lambda will be re-evaluated each time a terminal operator is used on the flow.
 */
@ExperimentalCoroutinesApi
public inline fun <reified T : Any> (() -> ReadableStream).toFlow(): Flow<T> = callbackFlow {
    flowHelper(this@toFlow())
}

/**
 * Convert a NodeJS stream to a flow.  **The resulting flow can only be consumed once**.
 *
 * For a repeatable flow use `{ stream }.toFlow()`.
 */
@ExperimentalCoroutinesApi
public inline fun <reified T : Any> ReadableStream.toFlow(): Flow<T> =
    callbackFlow<T> {
        flowHelper(this@toFlow)
    }
