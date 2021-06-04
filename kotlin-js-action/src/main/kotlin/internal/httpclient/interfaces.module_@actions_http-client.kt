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

public external interface IHeaders {
    @nativeGetter
    public operator fun get(key: String): Any?

    @nativeSetter
    public operator fun set(key: String, value: Any)
}

public external interface IHttpClient {
    public fun options(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public fun get(requestUrl: String, additionalHeaders: IHeaders = definedExternally): Promise<IHttpClientResponse>
    public fun del(requestUrl: String, additionalHeaders: IHeaders = definedExternally): Promise<IHttpClientResponse>
    public fun post(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public fun patch(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public fun put(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public fun sendStream(
        verb: String,
        requestUrl: String,
        stream: ReadableStream,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public fun request(verb: String, requestUrl: String, data: String, headers: IHeaders): Promise<IHttpClientResponse>
    public fun request(
        verb: String,
        requestUrl: String,
        data: ReadableStream,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    public fun requestRaw(info: IRequestInfo, data: String): Promise<IHttpClientResponse>
    public fun requestRaw(info: IRequestInfo, data: ReadableStream): Promise<IHttpClientResponse>
    public fun requestRawWithCallback(
        info: IRequestInfo,
        data: String,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    public fun requestRawWithCallback(
        info: IRequestInfo,
        data: ReadableStream,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )
}

public external interface IRequestHandler {
    public fun prepareRequest(options: RequestOptions)
    public fun canHandleAuthentication(response: IHttpClientResponse): Boolean
    public fun handleAuthentication(
        httpClient: IHttpClient,
        requestInfo: IRequestInfo,
        objs: Any
    ): Promise<IHttpClientResponse>
}

public external interface IHttpClientResponse {
    public var message: IncomingMessage
    public fun readBody(): Promise<String>
}

public external interface IRequestInfo {
    public var options: RequestOptions
    public var parsedUrl: URL
    public var httpModule: Any
}

public external interface IRequestOptions {
    public var headers: IHeaders?
        get() = definedExternally
        set(value) = definedExternally
    public var socketTimeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    public var ignoreSslError: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var allowRedirects: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var allowRedirectDowngrade: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var maxRedirects: Number?
        get() = definedExternally
        set(value) = definedExternally
    public var maxSockets: Number?
        get() = definedExternally
        set(value) = definedExternally
    public var keepAlive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var deserializeDates: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var allowRetries: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var maxRetries: Number?
        get() = definedExternally
        set(value) = definedExternally
}

public external interface ITypedResponse<T> {
    public var statusCode: Number
    public var result: T?
    public var headers: Any
}