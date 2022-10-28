@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/http-client")
@file:JsNonModule

package internal.httpclient

import NodeJS.ReadableStream
import http.IncomingMessage
import http.RequestOptions
import org.w3c.dom.url.URL
import kotlin.js.Promise

internal external interface IHeaders {
    @nativeGetter
    operator fun get(key: String): Any?

    @nativeSetter
    operator fun set(key: String, value: Any)
}

internal external interface IHttpClient {
    fun options(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    fun get(requestUrl: String, additionalHeaders: IHeaders = definedExternally): Promise<IHttpClientResponse>
    fun del(requestUrl: String, additionalHeaders: IHeaders = definedExternally): Promise<IHttpClientResponse>
    fun post(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    fun patch(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    fun put(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    fun sendStream(
        verb: String,
        requestUrl: String,
        stream: ReadableStream,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    fun request(verb: String, requestUrl: String, data: String, headers: IHeaders): Promise<IHttpClientResponse>
    fun request(
        verb: String,
        requestUrl: String,
        data: ReadableStream,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    fun requestRaw(info: IRequestInfo, data: String): Promise<IHttpClientResponse>
    fun requestRaw(info: IRequestInfo, data: ReadableStream): Promise<IHttpClientResponse>
    fun requestRawWithCallback(
        info: IRequestInfo,
        data: String,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    fun requestRawWithCallback(
        info: IRequestInfo,
        data: ReadableStream,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )
}

internal external interface IRequestHandler {
    fun prepareRequest(options: RequestOptions)
    fun canHandleAuthentication(response: IHttpClientResponse): Boolean
    fun handleAuthentication(
        httpClient: IHttpClient,
        requestInfo: IRequestInfo,
        objs: Any
    ): Promise<IHttpClientResponse>
}

internal external interface IHttpClientResponse {
    var message: IncomingMessage
    fun readBody(): Promise<String>
}

internal external interface IRequestInfo {
    var options: RequestOptions
    var parsedUrl: URL
    var httpModule: Any
}

internal external interface IRequestOptions {
    var headers: IHeaders?
        get() = definedExternally
        set(value) = definedExternally
    var socketTimeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreSslError: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var allowRedirects: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var allowRedirectDowngrade: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxRedirects: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxSockets: Number?
        get() = definedExternally
        set(value) = definedExternally
    var keepAlive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var deserializeDates: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var allowRetries: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxRetries: Number?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface ITypedResponse<T> {
    var statusCode: Number
    var result: T?
    var headers: Any
}