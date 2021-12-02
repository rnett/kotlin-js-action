package com.rnett.action.serialization

import com.rnett.action.httpclient.use
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class TestResponse(val slideshow: Slideshow)

@Serializable
data class Slideshow(val title: String, val author: String, val date: String, val slides: List<Slide>)

@Serializable
data class Slide(val title: String, val type: String, val items: List<String> = emptyList())

@Serializable
data class TestPostResponse<T>(val json: T)

@OptIn(DelicateCoroutinesApi::class)
class HttpClientTest {
    @Test
    fun testGet() = runTest {
        JsonHttpClient().use { client ->
            val response = client.get("https://httpbin.org/json")
            assertEquals(200, response.statusCode)
            val data = response.readJsonBody<TestResponse>()
            assertEquals("Yours Truly", data.slideshow.author)
        }
    }

    @Test
    fun testPost() = runTest {
        JsonHttpClient(Json {
            this.ignoreUnknownKeys = true
        }).use { client ->
            val slide = Slide("Nothing", "all")
            val response = client.postJson("https://httpbin.org/post", slide)
            assertEquals(200, response.statusCode)
            val data = response.readJsonBody<TestPostResponse<Slide>>().json
            assertEquals(slide, data)
        }
    }

    @Test
    fun testPostStreaming() = runTest {
        JsonHttpClient(Json {
            this.ignoreUnknownKeys = true
        }).use { client ->
            val slide = Slide("Nothing", "all")
            val slides = flowOf(slide)
            val response = client.postJson("https://httpbin.org/post", slides)
            assertEquals(200, response.statusCode)
            val data = response.readJsonBody<TestPostResponse<Slide>>().json
            assertEquals(slide, data)
        }
    }

}