package com.rnett.action.serialization

import NodeJS.ReadableStream
import com.rnett.action.httpclient.BaseHttpClient
import com.rnett.action.httpclient.HeaderProvider
import com.rnett.action.httpclient.HttpClient
import com.rnett.action.httpclient.HttpClientBuilder
import com.rnett.action.httpclient.HttpResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A HTTP response that you can get JSON deserialized responses from.
 */
public class JsonHttpResponse(response: HttpResponse, @PublishedApi internal val json: Json) :
    HttpResponse by response {
    public suspend inline fun <reified T> readJsonBody(): T = json.decodeFromString(readBody())
}

/**
 * Get a Http client with json support wrapping this client.
 *
 * Closing the returned client will not close the wrapped client.
 */
public fun HttpClient.json(json: Json = Json): JsonHttpClient = if (this is JsonHttpClient && this.json == json)
    this
else
    JsonHttpClient(json, this, false)

/**
 * A [`@actions/http-client`](https://github.com/actions/http-client) based Http client that uses
 * kotlinx serialization Json parsing and adds `Accept` (always) and `Content-Type` (*Json methods) headers.
 */
public inline fun JsonHttpClient(json: Json = Json, builder: HttpClientBuilder.() -> Unit = {}): JsonHttpClient {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return JsonHttpClient(
        json,
        HttpClient {
            builder()
            addHeader("accept", "application/json")
        }
    )
}

/**
 * A [`@actions/http-client`](https://github.com/actions/http-client) based Http client that uses
 * kotlinx serialization Json parsing and adds `Accept` (always) and `Content-Type` (*Json methods) headers.
 */
public class JsonHttpClient @PublishedApi internal constructor(
    @PublishedApi internal val json: Json,
    private val client: HttpClient,
    private val closeClient: Boolean = true
) :
    BaseHttpClient<JsonHttpResponse> {

    override suspend fun request(verb: String, url: String, data: String, headers: HeaderProvider): JsonHttpResponse =
        JsonHttpResponse(client.request(verb, url, data, headers), json)

    override suspend fun request(
        verb: String,
        url: String,
        data: ReadableStream,
        headers: HeaderProvider
    ): JsonHttpResponse = JsonHttpResponse(client.request(verb, url, data, headers), json)

    public suspend inline fun <reified T> postJson(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("post", url, data, headers = headers)

    public suspend inline fun <reified T> postJson(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("post", url, data, headers = headers)

    public suspend inline fun <reified T> putJson(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("put", url, data, headers = headers)

    public suspend inline fun <reified T> putJson(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("put", url, data, headers = headers)

    public suspend inline fun <reified T> patchJson(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("patch", url, data, headers = headers)

    public suspend inline fun <reified T> patchJson(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): JsonHttpResponse =
        requestJson("patch", url, data, headers = headers)

    public suspend inline fun <reified T> requestJson(
        verb: String,
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider {}
    ): JsonHttpResponse {
        val response =
            request(verb, url, json.encodeToString(data), headers + { add("content-type", "application/json") })
        return JsonHttpResponse(response, json)
    }

    public suspend inline fun <reified T> requestJson(
        verb: String,
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider {}
    ): JsonHttpResponse = coroutineScope {
        val response = request(
            verb,
            url,
            data.map { json.encodeToString(it) },
            headers + { add("content-type", "application/json") })
        JsonHttpResponse(response, json)
    }

    override fun close() {
        if (closeClient)
            client.close()
    }
}