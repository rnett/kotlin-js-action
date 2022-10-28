package com.rnett.action

import Buffer
import NodeJS.ReadableStream
import NodeJS.WritableStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import stream.TransformCallback


/**
 * Convert a flow to a stream by writing the flow's elements to an [internal.PassThrough].
 *
 * Collects the flow in [GlobalScope] by default, eventually [scope] will become a context receiver.
 *
 * TODO make [scope] a context receiver.
 */
@OptIn(DelicateCoroutinesApi::class)
public suspend fun Flow<String>.toStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.PassThrough()
    launch {
        this@toStream.onCompletion { stream.end() }
            .collect {
                stream.writeSuspending(it)
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
@OptIn(DelicateCoroutinesApi::class)
public suspend fun Flow<ByteArray>.toStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.PassThrough()
    launch {
        this@toStream.onCompletion { stream.end() }
            .collect {
                stream.writeSuspending(it)
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
@OptIn(DelicateCoroutinesApi::class)
public suspend fun Flow<Buffer>.toStream(scope: CoroutineScope = GlobalScope): internal.Readable = scope.run {
    val stream = internal.PassThrough()
    launch {
        this@toStream.onCompletion { stream.end() }
            .collect {
                stream.writeSuspending(it)
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
@Suppress("SuspendFunctionOnCoroutineScope")
@OptIn(DelicateCoroutinesApi::class)
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
                stream.writeSuspending(it)
            }
    }
    stream
}

@PublishedApi
@ExperimentalCoroutinesApi
@Suppress("SuspendFunctionOnCoroutineScope")
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

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: String) {
    suspendCancellableCoroutine<Unit> {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: Buffer) {
    suspendCancellableCoroutine<Unit> {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: String, encoding: String) {
    suspendCancellableCoroutine<Unit> {
        this.write(buffer, encoding, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun internal.Duplex.writeSuspending(buffer: Any) {
    suspendCancellableCoroutine<Unit> {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun internal.Duplex.writeSuspending(buffer: Any, encoding: String) {
    suspendCancellableCoroutine<Unit> {
        this.write(buffer, encoding, it::cancelIfError)
    }
}