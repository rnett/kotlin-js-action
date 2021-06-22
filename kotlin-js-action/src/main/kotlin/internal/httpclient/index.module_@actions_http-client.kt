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
import http.Agent
import http.IncomingMessage
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

internal external open class HttpClientResponse(message: IncomingMessage) : IHttpClientResponse {
    override var message: IncomingMessage
    override fun readBody(): Promise<String>
}

internal external fun isHttps(requestUrl: String): Boolean

internal external open class HttpClient(
    userAgent: String = definedExternally,
    handlers: Array<IRequestHandler> = definedExternally,
    requestOptions: IRequestOptions = definedExternally
) {
    open var userAgent: String?
    open var handlers: Array<IRequestHandler>
    open var requestOptions: IRequestOptions
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
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun get(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun del(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun post(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun patch(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun put(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun head(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun sendStream(
        verb: String,
        requestUrl: String,
        stream: ReadableStream,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    open fun <T> getJson(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    open fun <T> postJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    open fun <T> putJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    open fun <T> patchJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    open fun request(
        verb: String,
        requestUrl: String,
        data: String,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    open fun request(
        verb: String,
        requestUrl: String,
        data: ReadableStream,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    open fun dispose()
    open fun requestRaw(info: IRequestInfo, data: String): Promise<IHttpClientResponse>
    open fun requestRaw(info: IRequestInfo, data: ReadableStream): Promise<IHttpClientResponse>
    open fun requestRawWithCallback(
        info: IRequestInfo,
        data: String,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    open fun requestRawWithCallback(
        info: IRequestInfo,
        data: ReadableStream,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    open fun getAgent(serverUrl: String): Agent
    internal open var _prepareRequest: Any
    internal open var _mergeHeaders: Any
    internal open var _getExistingOrDefaultHeader: Any
    internal open var _getAgent: Any
    internal open var _performExponentialBackoff: Any
    internal open var _processResponse: Any

    companion object {
        internal var dateTimeDeserializer: Any
    }
}