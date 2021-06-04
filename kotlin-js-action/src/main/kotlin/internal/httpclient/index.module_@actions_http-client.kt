@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import http.IncomingMessage
import NodeJS.ReadableStream
import http.Agent

public external enum class HttpCodes {
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

public external enum class Headers {
    Accept /* = "accept" */,
    ContentType /* = "content-type" */
}

public external enum class MediaTypes {
    ApplicationJson /* = "application/json" */
}

public external fun getProxyUrl(serverUrl: String): String

public typealias HttpClientError = Error

public external open class HttpClientResponse(message: IncomingMessage) : IHttpClientResponse {
    override var message: IncomingMessage
    override fun readBody(): Promise<String>
}

public external fun isHttps(requestUrl: String): Boolean

public external open class HttpClient(
    userAgent: String = definedExternally,
    handlers: Array<IRequestHandler> = definedExternally,
    requestOptions: IRequestOptions = definedExternally
) {
    internal open var userAgent: String?
    internal open var handlers: Array<IRequestHandler>
    internal open var requestOptions: IRequestOptions
    internal open var _ignoreSslError: Any
    internal open var _socketTimeout: Any
    internal open var _allowRedirects: Any
    internal open var _allowRedirectDowngrade: Any
    internal open var _maxRedirects: Any
    internal open var _allowRetries: Any
    internal open var _maxRetries: Any
    internal open var _agent: Any
    internal open var _proxyAgent: Any
    internal open var _keepAlive: Any
    internal open var _disposed: Any
    public open fun options(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun get(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun del(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun post(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun patch(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun put(
        requestUrl: String,
        data: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun head(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun sendStream(
        verb: String,
        requestUrl: String,
        stream: ReadableStream,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<IHttpClientResponse>

    public open fun <T> getJson(
        requestUrl: String,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    public open fun <T> postJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    public open fun <T> putJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    public open fun <T> patchJson(
        requestUrl: String,
        obj: Any,
        additionalHeaders: IHeaders = definedExternally
    ): Promise<ITypedResponse<T>>

    public open fun request(
        verb: String,
        requestUrl: String,
        data: String,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    public open fun request(
        verb: String,
        requestUrl: String,
        data: ReadableStream,
        headers: IHeaders
    ): Promise<IHttpClientResponse>

    public open fun dispose()
    public open fun requestRaw(info: IRequestInfo, data: String): Promise<IHttpClientResponse>
    public open fun requestRaw(info: IRequestInfo, data: ReadableStream): Promise<IHttpClientResponse>
    public open fun requestRawWithCallback(
        info: IRequestInfo,
        data: String,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    public open fun requestRawWithCallback(
        info: IRequestInfo,
        data: ReadableStream,
        onResult: (err: Any, res: IHttpClientResponse) -> Unit
    )

    public open fun getAgent(serverUrl: String): Agent
    internal open var _prepareRequest: Any
    internal open var _mergeHeaders: Any
    internal open var _getExistingOrDefaultHeader: Any
    internal open var _getAgent: Any
    internal open var _performExponentialBackoff: Any
    internal open var _processResponse: Any

    public companion object {
        internal var dateTimeDeserializer: Any
    }
}