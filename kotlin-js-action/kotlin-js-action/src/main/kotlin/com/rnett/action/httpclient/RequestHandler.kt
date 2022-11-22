package com.rnett.action.httpclient

import com.rnett.action.JsObject
import internal.httpclient.HttpClientResponse
import internal.httpclient.RequestInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.js.set
import node.ReadableStream
import node.http.ClientRequestArgs
import node.http.OutgoingHttpHeaders
import node.http.RequestOptions
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
        data: Any?,
        options: ClientRequestArgs
    ): HttpResponse {
        error("Can not handle authentication")
    }
}

@OptIn(DelicateCoroutinesApi::class)
internal fun RequestHandler.toInternal() = object : internal.httpclient.RequestHandler {
    override fun prepareRequest(options: RequestOptions) {
        options.prepareRequest(WrappedClientRequestArgs(options))
    }

    override fun canHandleAuthentication(response: HttpClientResponse): Boolean {
        return this@toInternal.canHandleAuthentication(HttpResponseImpl(response))
    }

    override fun handleAuthentication(
        httpClient: internal.httpclient.HttpClient,
        requestInfo: RequestInfo,
        data: Any?
    ): Promise<HttpClientResponse> {
        return GlobalScope.promise {
            this@toInternal.handleAuthentication(
                WrappedClient(httpClient),
                requestInfo.parsedUrl,
                data,
                requestInfo.options
            ).toInternal()
        }
    }

}

internal fun Map<String, String>.toIHeaders(): OutgoingHttpHeaders = JsObject {
    this@toIHeaders.forEach {
        this[it.key] = it.value
    }
}