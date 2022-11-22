@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/http-client")
@file:JsNonModule

package internal.httpclient

import node.http.IncomingHttpHeaders
import node.http.OutgoingHttpHeaders
import org.w3c.dom.url.URL
import kotlin.js.Promise

internal external interface RequestOptions {
    var headers: OutgoingHttpHeaders?
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

internal external interface RequestHandler {
    fun prepareRequest(options: node.http.RequestOptions)
    fun canHandleAuthentication(response: HttpClientResponse): Boolean
    fun handleAuthentication(httpClient: HttpClient, requestInfo: RequestInfo, data: Any?): Promise<HttpClientResponse>
}

internal external interface RequestInfo {
    var options: node.http.RequestOptions
    var parsedUrl: URL
    var httpModule: Any
}

internal external interface TypedResponse<T> {
    var statusCode: Number
    var result: T?
    var headers: IncomingHttpHeaders
}