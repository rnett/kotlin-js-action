@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/http-client")
@file:JsNonModule

package internal.httpclient

import node.ReadableStream
import node.http.Agent
import node.http.IncomingMessage
import node.http.OutgoingHttpHeaders
import kotlin.js.Promise

internal external enum class HttpCodes {
    OK /* = 200 */,
    MultipleChoices /* = 300 */,
    MovedPermanently /* = 301 */,
    ResourceMoved /* = 302 */,
    SeeOther /* = 303 */,
    NotModified /* = 304 */,
    UseProxy /* = 305 */,
    SwitchProxy /* = 306 */,
    TemporaryRedirect /* = 307 */,
    PermanentRedirect /* = 308 */,
    BadRequest /* = 400 */,
    Unauthorized /* = 401 */,
    PaymentRequired /* = 402 */,
    Forbidden /* = 403 */,
    NotFound /* = 404 */,
    MethodNotAllowed /* = 405 */,
    NotAcceptable /* = 406 */,
    ProxyAuthenticationRequired /* = 407 */,
    RequestTimeout /* = 408 */,
    Conflict /* = 409 */,
    Gone /* = 410 */,
    TooManyRequests /* = 429 */,
    InternalServerError /* = 500 */,
    NotImplemented /* = 501 */,
    BadGateway /* = 502 */,
    ServiceUnavailable /* = 503 */,
    GatewayTimeout /* = 504 */
}

internal external enum class Headers {
    Accept /* = "accept" */,
    ContentType /* = "content-type" */
}

internal external enum class MediaTypes {
    ApplicationJson /* = "application/json" */
}

internal external fun getProxyUrl(serverUrl: String): String

internal external open class HttpClientResponse(message: IncomingMessage) {
    open var message: IncomingMessage
    open fun readBody(): Promise<String>
}

internal external fun isHttps(requestUrl: String): Boolean

internal open external class HttpClient(
    userAgent: String = definedExternally,
    handlers: Array<RequestHandler> = definedExternally,
    requestOptions: internal.httpclient.RequestOptions = definedExternally
) {
    open var userAgent: String?
    open var handlers: Array<RequestHandler>
    open var requestOptions: internal.httpclient.RequestOptions?
    open var _ignoreSslError: Any
    open var _socketTimeout: Any
    open var _allowRedirects: Any
    open var _allowRedirectDowngrade: Any
    open var _maxRedirects: Any
    open var _allowRetries: Any
    open var _maxRetries: Any
    open var _agent: Any
    open var _proxyAgent: Any
    open var _keepAlive: Any
    open var _disposed: Any
    open fun options(
        requestUrl: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun get(
        requestUrl: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun del(
        requestUrl: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun post(
        requestUrl: String,
        data: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun patch(
        requestUrl: String,
        data: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun put(
        requestUrl: String,
        data: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun head(
        requestUrl: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun sendStream(
        verb: String,
        requestUrl: String,
        stream: ReadableStream,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<HttpClientResponse>

    open fun <T> getJson(
        requestUrl: String,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<TypedResponse<T>>

    open fun <T> postJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<TypedResponse<T>>

    open fun <T> putJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<TypedResponse<T>>

    open fun <T> patchJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: OutgoingHttpHeaders = definedExternally
    ): Promise<TypedResponse<T>>

    open fun request(
        verb: String,
        requestUrl: String,
        data: String,
        headers: OutgoingHttpHeaders
    ): Promise<HttpClientResponse>

    open fun request(
        verb: String,
        requestUrl: String,
        data: ReadableStream,
        headers: OutgoingHttpHeaders
    ): Promise<HttpClientResponse>

    open fun dispose()
    open fun requestRaw(info: RequestInfo, data: String): Promise<HttpClientResponse>
    open fun requestRaw(info: RequestInfo, data: ReadableStream): Promise<HttpClientResponse>
    open fun requestRawWithCallback(
        info: RequestInfo,
        data: String,
        onResult: (err: Any, res: HttpClientResponse) -> Unit
    )

    open fun requestRawWithCallback(
        info: RequestInfo,
        data: ReadableStream,
        onResult: (err: Any, res: HttpClientResponse) -> Unit
    )

    open fun getAgent(serverUrl: String): Agent
    internal open var _prepareRequest: Any
    internal open var _mergeHeaders: Any
    internal open var _getExistingOrDefaultHeader: Any
    internal open var _getAgent: Any
    internal open var _performExponentialBackoff: Any
    internal open var _processResponse: Any
}