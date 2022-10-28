package com.rnett.action.httpclient

import NodeJS.ReadableStream
import com.rnett.action.JsObject
import http.ClientRequestArgs
import http.RequestOptions
import internal.httpclient.IHeaders
import internal.httpclient.IHttpClient
import internal.httpclient.IHttpClientResponse
import internal.httpclient.IRequestHandler
import internal.httpclient.IRequestInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.url.URL
import kotlin.js.Promise


/**
 * A handler that modifies outgoing requests and optionally handles authentication on 401s.
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
        client: HttpClient,
        url: URL,
        data: Any,
        options: ClientRequestArgs
    ): HttpResponse {
        error("Can not handle authentication")
    }
}

@OptIn(DelicateCoroutinesApi::class)
internal fun RequestHandler.toInternal() = object : IRequestHandler {
    override fun prepareRequest(options: RequestOptions) {
        options.prepareRequest(OutgoingJsHeaders(options))
    }

    override fun canHandleAuthentication(response: IHttpClientResponse): Boolean {
        return this@toInternal.canHandleAuthentication(HttpResponseImpl(response))
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
            ).toInternal()
        }
    }

}

internal fun Map<String, String>.toIHeaders(): IHeaders = JsObject {
    this@toIHeaders.forEach {
        this[it.key] = it.value
    }
}