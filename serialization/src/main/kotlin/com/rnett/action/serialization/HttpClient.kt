package com.rnett.action.serialization

import com.rnett.action.httpclient.HeaderProvider
import com.rnett.action.httpclient.HttpClient
import com.rnett.action.httpclient.HttpClientBuilder
import com.rnett.action.httpclient.HttpResponse
import com.rnett.action.httpclient.IHttpResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import stream.internal

/**
 * A HTTP response that you can get typed data from.
 */
public class TypedHttpResponse(response: HttpResponse, @PublishedApi internal val json: Json) :
    IHttpResponse by response {
    public suspend inline fun <reified T> readTypedBody(): T = json.decodeFromString(readBody())
}

/**
 * A Http client that uses kotlinx serialization Json parsing and adds `Accept` and `Content-Type` headers.
 */
public class JsonHttpClient(@PublishedApi internal val json: Json = Json, builder: HttpClientBuilder.() -> Unit = {}) :
    HttpClient({
        builder()
        addHeader("accept", "application/json")
    }) {

    public suspend inline fun headTyped(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("head", url, "", headers = headers)

    public suspend inline fun getTyped(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("get", url, "", headers = headers)

    public suspend inline fun optionsTyped(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("options", url, "", headers = headers)

    public suspend inline fun delTyped(
        url: String,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("del", url, "", headers = headers)

    public suspend inline fun <reified T> postTyped(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("post", url, data, headers = headers)

    public suspend inline fun <reified T> postTyped(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("post", url, data, headers = headers)

    public suspend inline fun <reified T> putTyped(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("put", url, data, headers = headers)

    public suspend inline fun <reified T> putTyped(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("put", url, data, headers = headers)

    public suspend inline fun <reified T> patchTyped(
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("patch", url, data, headers = headers)

    public suspend inline fun <reified T> patchTyped(
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider { }
    ): TypedHttpResponse =
        requestTyped("patch", url, data, headers = headers)

    public suspend inline fun <reified T> requestTyped(
        verb: String,
        url: String,
        data: T,
        headers: HeaderProvider = HeaderProvider {}
    ): TypedHttpResponse {
        val response =
            request(verb, url, json.encodeToString(data), headers + { add("content-type", "application/json") })
        return TypedHttpResponse(response, json)
    }

    public suspend inline fun <reified T> requestTyped(
        verb: String,
        url: String,
        data: Flow<T>,
        headers: HeaderProvider = HeaderProvider {}
    ): TypedHttpResponse = coroutineScope {
        val stream = internal.PassThrough()
        launch {
            data.onCompletion { stream.end() }
                .collect {
                    stream.write(json.encodeToString(it))
                }
        }
        val response = request(verb, url, stream, headers + { add("content-type", "application/json") })
        stream.destroy()
        TypedHttpResponse(response, json)
    }
}