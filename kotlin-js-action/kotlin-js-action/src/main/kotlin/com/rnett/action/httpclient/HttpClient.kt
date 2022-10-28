package com.rnett.action.httpclient

import com.rnett.action.JsObject
import com.rnett.action.jsEntries
import com.rnett.action.toStream
import internal.httpclient.IHttpClient
import internal.httpclient.ITypedResponse
import internal.httpclient.getProxyUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import node.ReadableStream
import node.buffer.Buffer
import node.events.Event
import node.stream.PassThrough
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import internal.httpclient.HttpClient as JsHttpClient

/**
 * Get the GitHub actions proxy URL.
 */
public fun githubProxyUrl(url: String): String = getProxyUrl(url)

private fun buildHttpClient(
    handlers: List<RequestHandler>,
    userAgent: String?,
    defaultHeaders: Map<String, String>,
    socketTimeout: Int?,
    ignoreSslError: Boolean?,
    allowRedirects: Boolean?,
    allowRedirectDowngrade: Boolean?,
    maxRedirects: Int?,
    maxSockets: Int?,
    keepAlive: Boolean?,
    deserializeDates: Boolean?,
    allowRetries: Boolean?,
    maxRetries: Int?
): JsHttpClient =
    JsHttpClient(userAgent.asDynamic(), handlers.map { it.toInternal() }.toTypedArray(), requestOptions = JsObject {
        this.headers = defaultHeaders.toIHeaders()
        this.socketTimeout = socketTimeout
        this.ignoreSslError = ignoreSslError
        this.allowRedirects = allowRedirects
        this.allowRedirectDowngrade = allowRedirectDowngrade
        this.maxRedirects = maxRedirects
        this.maxSockets = maxSockets
        this.keepAlive = keepAlive
        this.deserializeDates = deserializeDates
        this.allowRetries = allowRetries
        this.maxRetries = maxRetries
    })

/**
 * A builder for http client configuration.
 */
public class HttpClientBuilder @PublishedApi internal constructor() {

    private val mapHeaders = MapHeaders()

    /**
     * The headers added to every request.
     */
    public val defaultHeaders: MutableHeaders = mapHeaders
    public var userAgent: String? = null
    public var socketTimeout: Int? = null
    public var ignoreSslError: Boolean? = null
    public var allowRedirects: Boolean? = null
    public var allowRedirectDowngrade: Boolean? = null
    public var maxRedirects: Int? = null
    public var maxSockets: Int? = null
    public var keepAlive: Boolean? = null
    public var deserializeDates: Boolean? = null
    public var allowRetries: Boolean? = null
    public var maxRetries: Int? = null

    private val handlers = mutableListOf<RequestHandler>()

    /**
     * Add a default header, adding it to the existing header after a `,` if a this header has already been set.
     */
    public fun addHeader(name: String, value: String) {
        defaultHeaders.add(name, value)
    }

    /**
     * Set a default header.
     */
    public fun header(name: String, value: String) {
        defaultHeaders[name] = value
    }

    /**
     * Add default headers.
     */
    public inline fun headers(builder: MutableHeaders.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        defaultHeaders.builder()
    }

    /**
     * Add default headers.
     */
    public fun headers(builder: HeaderProvider) {
        defaultHeaders += builder
    }

    /**
     * Add a request handler.
     */
    public fun handler(handler: RequestHandler) {
        handlers += handler
    }

    /**
     * Add a basic authentication header.
     */
    public fun basicAuth(username: String, password: String) {
        handler(BasicAuthHandler(username, password))
    }

    /**
     * Add a bearer authentication header.
     */
    public fun bearerAuth(token: String) {
        handler(BearerAuthHandler(token))
    }

    internal fun build() = buildHttpClient(
        handlers,
        userAgent,
        mapHeaders.toMap(),
        socketTimeout,
        ignoreSslError,
        allowRedirects,
        allowRedirectDowngrade,
        maxRedirects,
        maxSockets,
        keepAlive,
        deserializeDates,
        allowRetries,
        maxRetries
    )
}

/**
 * A minimal HTTP client, based on [`@actions/http-client`](https://github.com/actions/http-client).
 */
public interface BaseHttpClient<out T : HttpResponse> {
    public suspend fun head(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("head", url, "", headers = headers)

    public suspend fun get(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("get", url, "", headers = headers)

    public suspend fun options(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("options", url, "", headers = headers)

    public suspend fun del(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("del", url, "", headers = headers)

    public suspend fun post(
        url: String,
        data: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("post", url, data, headers = headers)

    public suspend fun post(
        url: String,
        data: ReadableStream,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("post", url, data, headers = headers)

    public suspend fun post(
        url: String,
        data: Flow<String>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("post", url, data, headers = headers)

    public suspend fun post(
        url: String,
        data: Flow<Buffer>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("post", url, data, headers = headers)

    public suspend fun put(
        url: String,
        data: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("put", url, data, headers = headers)

    public suspend fun put(
        url: String,
        data: ReadableStream,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("put", url, data, headers = headers)

    public suspend fun put(
        url: String,
        data: Flow<String>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("put", url, data, headers = headers)

    public suspend fun put(
        url: String,
        data: Flow<Buffer>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("put", url, data, headers = headers)

    public suspend fun patch(
        url: String,
        data: String,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("patch", url, data, headers = headers)

    public suspend fun patch(
        url: String,
        data: ReadableStream,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("patch", url, data, headers = headers)

    public suspend fun patch(
        url: String,
        data: Flow<String>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("patch", url, data, headers = headers)

    public suspend fun patch(
        url: String,
        data: Flow<Buffer>,
        headers: HeaderProvider = HeaderProvider { }
    ): T =
        request("patch", url, data, headers = headers)

    public suspend fun request(
        verb: String,
        url: String,
        data: String,
        headers: HeaderProvider = HeaderProvider {}
    ): T

    public suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        headers: HeaderProvider = HeaderProvider {}
    ): T

    public suspend fun request(
        verb: String,
        url: String,
        data: Flow<String>,
        headers: HeaderProvider = HeaderProvider {}
    ): T = coroutineScope {
        request(verb, url, data.toStream(this), headers)
    }

    public suspend fun request(
        verb: String,
        url: String,
        data: Flow<Buffer>,
        headers: HeaderProvider = HeaderProvider {}
    ): T = coroutineScope {
        request(verb, url, data.toStream(this), headers)
    }

    public fun close() {
    }
}

internal class WrappedInterfaceClient(private val client: IHttpClient) : BaseHttpClient<HttpResponse> {
    override suspend fun request(
        verb: String,
        url: String,
        data: String,
        headers: HeaderProvider
    ): HttpResponse =
        client.request(verb.uppercase(), url, data, headers.toIHeaders()).await().let(::HttpResponseImpl)

    override suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        headers: HeaderProvider
    ): HttpResponse = coroutineScope {
        async { // hangs if removed
            val sendStream = PassThrough()
            data.pipe(sendStream.asDynamic(), JsObject {
                this.end = false
            })
            data.on(Event.CLOSE) { sendStream.destroy() }
            data.on(Event.END) { sendStream.destroy() }
            client.request(verb.uppercase(), url, sendStream, headers.toIHeaders()).await()
                .let(::HttpResponseImpl)
        }.await()
    }
}

/**
 * A typed HTTP response.  All casting is done in JS, so this is not type safe.
 */
@Deprecated("This method is not type safe, JsonHttpClient (in the serialization artifact) should be used instead")
public class JSTypedHttpResponse<T> internal constructor(response: ITypedResponse<T>) {
    public val statusCode: Int = response.statusCode.toInt()
    public val result: T? = response.result
    public val headers: Map<String, String> = jsEntries(response.headers).mapValues {
        when (val value = it.value as Any?) {
            is String -> value
            is Array<*> -> value.joinToString(", ")
            else -> error("Unknown header type $value")
        }
    }
}

/**
 * A HTTP client, based on [`@actions/http-client`](https://github.com/actions/http-client).
 */
public typealias HttpClient = BaseHttpClient<*>

/**
 * A HTTP client, based on [`@actions/http-client`](https://github.com/actions/http-client).
 */
public inline fun HttpClient(builder: HttpClientBuilder.() -> Unit = {}): HttpClient {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return HttpClientImpl(HttpClientBuilder().apply(builder))
}

/**
 * A HTTP client, based on [`@actions/http-client`](https://github.com/actions/http-client).
 */
@Deprecated(
    "Should use HttpClient instead, the only reason to use this is the raw JS Json request methods",
    replaceWith = ReplaceWith("HttpClient", "com.rnett.action.httpclient.HttpClient")
)
public class HttpClientImpl internal constructor(private val client: JsHttpClient) : BaseHttpClient<HttpResponse> {
    @PublishedApi
    internal constructor(builder: HttpClientBuilder) : this(builder.build())
    public constructor(builder: HttpClientBuilder.() -> Unit = {}) : this(HttpClientBuilder().apply(builder))

    override suspend fun request(
        verb: String,
        url: String,
        data: String,
        headers: HeaderProvider
    ): HttpResponse =
        client.request(verb.uppercase(), url, data, headers.toIHeaders()).await().let(::HttpResponseImpl)

    @Suppress("RedundantAsync")
    override suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        headers: HeaderProvider
    ): HttpResponse = coroutineScope {
        async { // hangs if removed
            val sendStream = PassThrough()
            data.pipe(sendStream.asDynamic(), JsObject {
                this.end = false
            })
            data.on(Event.CLOSE) { sendStream.destroy() }
            data.on(Event.END) { sendStream.destroy() }
            client.request(verb.uppercase(), url, sendStream, headers.toIHeaders()).await()
                .let(::HttpResponseImpl)
        }.await()
    }

    /**
     * GET a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, JsonHttpClient (in the serialization artifact) should be used instead")
    public suspend fun <T> getExternalTypedJson(
        url: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): JSTypedHttpResponse<T> =
        client.getJson<T>(url, additionalHeaders.toIHeaders()).await().let(::JSTypedHttpResponse)

    /**
     * POST JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, JsonHttpClient (in the serialization artifact) should be used instead")
    public suspend fun <T> postExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): JSTypedHttpResponse<T> =
        client.postJson<T>(url, data, additionalHeaders.toIHeaders()).await().let(::JSTypedHttpResponse)

    /**
     * PUT JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, JsonHttpClient (in the serialization artifact) should be used instead")
    public suspend fun <T> putExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): JSTypedHttpResponse<T> =
        client.putJson<T>(url, data, additionalHeaders.toIHeaders()).await().let(::JSTypedHttpResponse)

    /**
     * PATCH JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, JsonHttpClient (in the serialization artifact) should be used instead")
    public suspend fun <T> patchExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): JSTypedHttpResponse<T> =
        client.patchJson<T>(url, data, additionalHeaders.toIHeaders()).await().let(::JSTypedHttpResponse)


    override fun close() {
        client.dispose()
    }
}

/**
 * Use a HTTP client, then close it in `finally` block.  Like JVM's `use`.
 */
public inline fun <R, T : BaseHttpClient<*>> T.use(block: (T) -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        val result = block(this)
        return result
    } finally {
        close()
    }
}