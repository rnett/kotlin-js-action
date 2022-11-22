package com.rnett.action.httpclient

import internal.httpclient.HttpClientResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import node.http.IncomingMessage
import kotlin.js.Promise


/**
 * The response from a HTTP request.
 */
public interface HttpResponse {
    public suspend fun readBody(): String

    public val message: IncomingMessage
    public val headers: Headers
    public val statusCode: Int
    public val statusMessage: String
    public fun isSuccess(): Boolean
}

internal fun HttpResponse.toInternal(): HttpClientResponse =
    if (this is HttpResponseImpl)
        this.internal
    else
        object : HttpClientResponse(this@toInternal.message) {
            override var message: IncomingMessage = this@toInternal.message

            override fun readBody(): Promise<String> = GlobalScope.promise {
                this@toInternal.readBody()
            }
        }


/**
 * The response from a HTTP request.
 */
internal class HttpResponseImpl internal constructor(internal val internal: HttpClientResponse) : HttpResponse {
    override suspend fun readBody(): String = internal.readBody().await()

    override val message: IncomingMessage get() = internal.message

    override val headers: Headers = message.rawHeaders.asSequence().chunked(2) {
        it[0].lowercase() to it[1]
    }.toMap().let { MapHeaders(it) }

    override val statusCode: Int = message.statusCode!!.toInt()
    override val statusMessage: String get() = message.statusMessage!!
    override fun isSuccess(): Boolean = statusCode in 200..299
}