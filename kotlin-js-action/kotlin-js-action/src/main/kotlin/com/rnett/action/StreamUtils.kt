package com.rnett.action

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import node.ReadableStream
import node.WritableStream
import node.buffer.Buffer
import node.buffer.BufferEncoding
import node.stream.Duplex
import node.stream.PassThrough
import node.stream.Readable
import node.stream.Transform
import node.stream.TransformCallback
import node.stream.Writable
import kotlin.coroutines.resume


/**
 * Convert a flow to a stream by writing the flow's elements to an [internal.PassThrough].
 *
 * Collects the flow in [GlobalScope] by default, eventually [scope] will become a context receiver.
 *
 * TODO make [scope] a context receiver.
 */
@OptIn(DelicateCoroutinesApi::class)
public suspend fun Flow<String>.toStream(scope: CoroutineScope = GlobalScope): Readable = scope.run {
    val stream = PassThrough()
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
public suspend fun Flow<ByteArray>.toStream(scope: CoroutineScope = GlobalScope): Readable = scope.run {
    val stream = PassThrough()
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
public suspend fun Flow<Buffer>.toStream(scope: CoroutineScope = GlobalScope): Readable = scope.run {
    val stream = PassThrough()
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
public suspend fun Flow<Any>.toObjectStream(scope: CoroutineScope = GlobalScope): Readable = scope.run {
    val stream = Transform(JsObject {
        this.readableObjectMode = true
        this.writableObjectMode = true

        transform = { chunk: Any, encoding: BufferEncoding, callback: TransformCallback ->
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
    stream.pipe(Writable(JsObject {
        objectMode = true

        write = write@{ chunk, encoding, callback ->
            if (chunk !is T) {
                val exception = ClassCastException("Can't cast value to class ${T::class}: ${chunk::class} with value $chunk")
                callback(Error("Wrong element type", exception))
                channel.close(exception)
                return@write
            }
            launch {
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

        destroy = { error, callback ->
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
    callbackFlow {
        flowHelper(this@toFlow)
    }

internal fun CancellableContinuation<Unit>.cancelIfError(err: Error?) {
    if (err != null)
        cancel(err)
    else
        resume(Unit)
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: String) {
    suspendCancellableCoroutine {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: Buffer) {
    suspendCancellableCoroutine {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun WritableStream.writeSuspending(buffer: String, encoding: BufferEncoding) {
    suspendCancellableCoroutine {
        this.write(buffer, encoding, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun Duplex.writeSuspending(buffer: Any) {
    suspendCancellableCoroutine {
        this.write(buffer, it::cancelIfError)
    }
}

/**
 * Write to a stream, suspending until the write completes.
 */
public suspend fun Duplex.writeSuspending(buffer: Any, encoding: BufferEncoding) {
    suspendCancellableCoroutine {
        this.write(buffer, encoding, it::cancelIfError)
    }
}

/**
 * Get the write stream as a [Writable].  This is just a cast.
 * See [A note on process I/O](https://nodejs.org/api/process.html#a-note-on-process-io) for details.
 */
public val node.process.WriteStream.writable: Writable get() = this.asDynamic() as Writable

/**
 * Get the read stream as a [Readable].  This is just a cast.
 * See [A note on process I/O](https://nodejs.org/api/process.html#a-note-on-process-io) for details.
 */
public val node.process.ReadStream.readable: Readable get() = this.asDynamic() as Readable


public inline fun Buffer.Companion.from(data: String, encoding: BufferEncoding): Buffer = asDynamic().from(data, encoding)

public fun String.encodeBase64(): String = Buffer.from(this).toString(BufferEncoding.base64)

public fun String.decodeBase64(): String = Buffer.from(this, BufferEncoding.base64).toString(BufferEncoding.utf8)