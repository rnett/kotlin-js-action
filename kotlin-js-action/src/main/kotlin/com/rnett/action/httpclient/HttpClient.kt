package com.rnett.action.httpclient

import NodeJS.ReadableStream
import com.rnett.action.JsObject
import com.rnett.action.jsEntries
import http.ClientRequestArgs
import http.IncomingMessage
import http.RequestOptions
import internal.httpclient.IHeaders
import internal.httpclient.IHttpClient
import internal.httpclient.IHttpClientResponse
import internal.httpclient.IRequestHandler
import internal.httpclient.IRequestInfo
import internal.httpclient.ITypedResponse
import internal.httpclient.getProxyUrl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import org.w3c.dom.url.URL
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.js.Promise
import internal.httpclient.HttpClient as JsHttpClient

/**
 * The response from a HTTP request.
 */
public class HttpResponse internal constructor(internal val internal: IHttpClientResponse) {
    public suspend fun readBody(): String = internal.readBody().await()

    public val message: IncomingMessage get() = internal.message

    //TODO use Headers type for non-case sensitive-ness?
    public val headers: Headers = message.rawHeaders.asSequence().chunked(2) {
        it[0] to it[1]
    }.toMap().let { MapHeaders(it) }
    public val statusCode: Int = message.statusCode.toInt()
    public val statusMessage: String get() = message.statusMessage
    public fun isSuccess(): Boolean = statusCode in 200..299
}

/**
 * A handler that modifies outgoing requests and optionally handles authentication.
 */
public fun interface RequestHandler {

    /**
     * Modify an outgoing request.
     */
    public fun ClientRequestArgs.prepareRequest(headers: MutableHeaders)

    /**
     * If this returns `true`, [handleAuthentication] must be implemented.
     */
    public fun canHandleAuthentication(response: HttpResponse): Boolean {
        return false
    }

    /**
     * Will not be called unless [canHandleAuthentication] is `true`.
     *
     * [data] will be [ReadableStream] or [String].
     */
    public suspend fun handleAuthentication(
        client: BasicHttpClient,
        url: URL,
        data: Any,
        options: ClientRequestArgs
    ): HttpResponse {
        error("Can not handle authentication")
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun RequestHandler.toInternal() = object : IRequestHandler {
    override fun prepareRequest(options: RequestOptions) {
        options.prepareRequest(OutgoingJsHeaders(options))
    }

    override fun canHandleAuthentication(response: IHttpClientResponse): Boolean {
        return this@toInternal.canHandleAuthentication(HttpResponse(response))
    }

    override fun handleAuthentication(
        httpClient: IHttpClient,
        requestInfo: IRequestInfo,
        objs: Any
    ): Promise<IHttpClientResponse> {
        return GlobalScope.promise {
            this@toInternal.handleAuthentication(
                WrappedInterfaceClient(httpClient),
                requestInfo.parsedUrl,
                objs,
                requestInfo.options
            ).internal
        }
    }

}

private fun Map<String, String>.toHeaders(): IHeaders = JsObject {
    this@toHeaders.forEach {
        this[it.key] = it.value
    }
}

private fun buildHttpClient(
    handlers: List<RequestHandler>,
    userAgent: String,
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
): JsHttpClient = JsHttpClient(userAgent, handlers.map { it.toInternal() }.toTypedArray(), requestOptions = JsObject {
    this.headers = defaultHeaders.toHeaders()
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

public fun githubProxyUrl(url: String): String = getProxyUrl(url)

/**
 * A builder for http client configuration.
 */
public class HttpClientBuilder internal constructor() {

    private val mapHeaders = MapHeaders()

    /**
     * The headers added to every request.
     */
    public val defaultHeaders: MutableHeaders = mapHeaders
    public var userAgent: String = "Github Actions"
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
 * Helpers for HTTP methods
 */
public open class EmptyMethodRequester internal constructor(
    protected val method: String,
    protected val client: BasicHttpClient
) {
    public suspend operator fun invoke(url: String, additionalHeaders: Map<String, String> = emptyMap()): HttpResponse =
        client.request(method, url, "", additionalHeaders)


    public suspend operator fun invoke(url: String, vararg additionalHeaders: Pair<String, String>): HttpResponse =
        client.request(method, url, "", *additionalHeaders)


    public suspend operator fun invoke(
        url: String,
        vararg additionalHeaders: HeaderProvider,
        oneMore: HeaderProvider = HeaderProvider { }
    ): HttpResponse =
        client.request(method, url, "", *additionalHeaders, oneMore = oneMore)
}

/**
 * Helpers for HTTP methods
 */
public class MethodRequester internal constructor(method: String, client: BasicHttpClient) :
    EmptyMethodRequester(method, client) {
    public suspend operator fun invoke(
        url: String,
        data: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): HttpResponse =
        client.request(method, url, data, additionalHeaders)


    public suspend operator fun invoke(
        url: String,
        data: String,
        vararg additionalHeaders: Pair<String, String>
    ): HttpResponse =
        client.request(method, url, data, *additionalHeaders)


    public suspend operator fun invoke(
        url: String,
        data: String,
        vararg additionalHeaders: HeaderProvider,
        oneMore: HeaderProvider = HeaderProvider { }
    ): HttpResponse =
        client.request(method, url, data, *additionalHeaders, oneMore = oneMore)

    public suspend operator fun invoke(
        url: String,
        data: ReadableStream,
        additionalHeaders: Map<String, String> = emptyMap()
    ): HttpResponse =
        client.request(method, url, data, additionalHeaders)


    public suspend operator fun invoke(
        url: String,
        data: ReadableStream,
        vararg additionalHeaders: Pair<String, String>
    ): HttpResponse =
        client.request(method, url, data, *additionalHeaders)


    public suspend operator fun invoke(
        url: String,
        data: ReadableStream,
        vararg additionalHeaders: HeaderProvider,
        oneMore: HeaderProvider = HeaderProvider { }
    ): HttpResponse =
        client.request(method, url, data, *additionalHeaders, oneMore = oneMore)
}

/**
 * A minimal HTTP client, based on [`@actions/httpclient`](https://github.com/actions/http-client).
 */
public interface BasicHttpClient {
    public val head: EmptyMethodRequester
        get() = EmptyMethodRequester("head", this)

    public val get: EmptyMethodRequester
        get() = EmptyMethodRequester("get", this)

    public val options: EmptyMethodRequester
        get() = EmptyMethodRequester("options", this)

    public val del: EmptyMethodRequester
        get() = EmptyMethodRequester("del", this)

    public val post: MethodRequester
        get() = MethodRequester("post", this)

    public val put: MethodRequester
        get() = MethodRequester("put", this)

    public val patch: MethodRequester
        get() = MethodRequester("patch", this)

    public suspend fun request(
        verb: String,
        url: String,
        data: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): HttpResponse

    public suspend fun request(
        verb: String,
        url: String,
        data: String,
        vararg additionalHeaders: HeaderProvider,
        oneMore: HeaderProvider = HeaderProvider {}
    ): HttpResponse {
        val headers = MapHeaders()
        additionalHeaders.forEach {
            headers += it
        }
        headers += oneMore
        return request(verb, url, data, headers.toMap())
    }

    public suspend fun request(
        verb: String,
        url: String,
        data: String,
        vararg additionalHeaders: Pair<String, String>
    ): HttpResponse = request(verb, url, data, additionalHeaders.toMap())

    public suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        additionalHeaders: Map<String, String> = emptyMap()
    ): HttpResponse

    public suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        vararg additionalHeaders: Pair<String, String>
    ): HttpResponse = request(verb, url, data, additionalHeaders.toMap())

    public suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        vararg additionalHeaders: HeaderProvider,
        oneMore: HeaderProvider = HeaderProvider {}
    ): HttpResponse {
        val headers = MapHeaders()
        additionalHeaders.forEach {
            headers += it
        }
        headers += oneMore
        return request(verb, url, data, headers.toMap())
    }
}

internal class WrappedInterfaceClient(private val client: IHttpClient) : BasicHttpClient {
    public override suspend fun request(
        verb: String,
        url: String,
        data: String,
        additionalHeaders: Map<String, String>
    ): HttpResponse =
        client.request(verb.uppercase(), url, data, additionalHeaders.toHeaders()).await().let(::HttpResponse)

    public override suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        additionalHeaders: Map<String, String>
    ): HttpResponse =
        client.sendStream(verb.uppercase(), url, data, additionalHeaders.toHeaders()).await().let(::HttpResponse)
}

/**
 * A typed HTTP response.  All casting is done in JS, so this is not type safe.
 */
@Deprecated("This method is not type safe, Kotlin serialization methods should be used instead")
public class TypedHttpResponse<T> internal constructor(response: ITypedResponse<T>) {
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
 * A HTTP client, based on [`@actions/httpclient`](https://github.com/actions/http-client).
 */
public class HttpClient internal constructor(private val client: JsHttpClient) : BasicHttpClient {
    public constructor(builder: HttpClientBuilder.() -> Unit = {}) : this(HttpClientBuilder().apply(builder).build())

    public override suspend fun request(
        verb: String,
        url: String,
        data: String,
        additionalHeaders: Map<String, String>
    ): HttpResponse =
        client.request(verb.uppercase(), url, data, additionalHeaders.toHeaders()).await().let(::HttpResponse)

    public override suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        additionalHeaders: Map<String, String>
    ): HttpResponse =
        client.request(verb.uppercase(), url, data, additionalHeaders.toHeaders()).await().let(::HttpResponse)

    /**
     * GET a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, Kotlin serialization methods should be used instead")
    public suspend fun <T> getExternalTypedJson(
        url: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): TypedHttpResponse<T> =
        client.getJson<T>(url, additionalHeaders.toHeaders()).await().let(::TypedHttpResponse)

    /**
     * POST JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, Kotlin serialization methods should be used instead")
    public suspend fun <T> postExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): TypedHttpResponse<T> =
        client.postJson<T>(url, data, additionalHeaders.toHeaders()).await().let(::TypedHttpResponse)

    /**
     * PUT JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, Kotlin serialization methods should be used instead")
    public suspend fun <T> putExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): TypedHttpResponse<T> =
        client.putJson<T>(url, data, additionalHeaders.toHeaders()).await().let(::TypedHttpResponse)

    /**
     * PATCH JSON stringified data and get a JSON response, casting it to the requested type in JS.  [T] should be an external interface, **no type checking is done**.
     *
     * [data] will be JSON stringified using JS methods.
     *
     * 404 will be mapped to `null`.
     */
    @Deprecated("This method is not type safe, Kotlin serialization methods should be used instead")
    public suspend fun <T> patchExternalTypedJson(
        url: String,
        data: Any,
        additionalHeaders: Map<String, String> = emptyMap()
    ): TypedHttpResponse<T> =
        client.patchJson<T>(url, data, additionalHeaders.toHeaders()).await().let(::TypedHttpResponse)

    public inline fun <R> use(block: (HttpClient) -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        val result = block(this)
        close()
        return result
    }

    public fun close() {
        client.dispose()
    }
}