package com.rnett.action

import com.rnett.action.httpclient.BasicAuthHandler
import com.rnett.action.httpclient.BearerAuthHandler
import com.rnett.action.httpclient.HeaderProvider
import com.rnett.action.httpclient.HttpClient
import com.rnett.action.httpclient.HttpResponse
import com.rnett.action.httpclient.PersonalAccessTokenAuthHandler
import com.rnett.action.httpclient.use
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestHttpClient : TestWithDir() {

    private suspend fun testHeaderHelper(
        methodName: String,
        method: suspend (String, HeaderProvider) -> HttpResponse,
        addAcceptHeader: Boolean
    ): HttpResponse {
        val url = "https://www.httpbin.org/${methodName.lowercase()}"
        val response = method(url) {
            if (addAcceptHeader)
                this["accept"] = "application/json"
        }

        assertEquals(200, response.statusCode)

        val body = Json.parseToJsonElement(response.readBody())
        assertEquals(body.jsonObject["headers"]?.jsonObject?.get("Accept"), JsonPrimitive("application/json"))

        return response
    }

    private suspend fun testStatusHelper(
        code: Int,
        method: suspend (String, HeaderProvider) -> HttpResponse,
    ): HttpResponse {
        val url = "https://www.httpbin.org/status/$code"
        val response = method(url) {
        }

        assertEquals(code, response.statusCode)

        return response
    }

    private suspend fun testHelper(
        methodName: String,
        method: suspend HttpClient.(String, HeaderProvider) -> HttpResponse
    ) {

        fun HttpClient.method(): suspend (String, HeaderProvider) -> HttpResponse =
            { url, headers -> this.method(url, headers) }

        HttpClient().use { client ->
            testStatusHelper(200, client.method())
            testStatusHelper(404, client.method())
            testStatusHelper(505, client.method())

            testHeaderHelper(methodName, client.method(), addAcceptHeader = true)
        }

        HttpClient {
            header("Accept", "application/json")
        }.use { client ->
            testHeaderHelper(methodName, client.method(), addAcceptHeader = false)
        }
    }

    private suspend fun testHelper(
        methodName: String,
        method: suspend HttpClient.(String, String, HeaderProvider) -> HttpResponse
    ) {
        testHelper(methodName) { url, headers -> method(url, "", headers) }
    }

    @Test
    fun testGet() = runTest { testHelper("get", HttpClient::get) }

    @Test
    fun testUserAgent() = runTest {
        HttpClient {
            userAgent = "test"
        }.use { client ->
            client.get("https://httpbin.org/user-agent").also {
                assertContains(it.readBody(), "\"user-agent\": \"test\"")
            }
        }
    }

    @Test
    fun testHeaders() = runTest {
        HttpClient {
            header("test1", "testData1")
            handler {
                it["test3"] = "testData3"
            }
        }.use { client ->
            client.get("https://httpbin.org/headers") { +mapOf("test2" to "testData2") }.let {
                val body = it.readBody()
                assertContains(body, "\"Test1\": \"testData1\"")
                assertContains(body, "\"Test2\": \"testData2\"")
                assertContains(body, "\"Test3\": \"testData3\"")
                println(it.headers)
                assertEquals("application/json", it.headers["content-type"])
            }
        }
    }

    @Test
    fun testOptions() = runTest {
        val status = HttpClient().use {
            it.options("https://www.httpbin.org/status/200")
        }.statusCode
        assertEquals(200, status)
    }

    @Test
    fun testDelete() = runTest { testHelper("delete", HttpClient::delete) }

    @Test
    fun testHead() = runTest {
        val status = HttpClient().use {
            it.head("https://www.httpbin.org/status/200")
        }.statusCode
        assertEquals(200, status)
    }

    @Test
    fun testBase64() {
        val encoded = "dGVzdDp0ZXN0"
        val decoded = "test:test"
        assertEquals(encoded, decoded.encodeBase64())
        assertEquals(decoded, encoded.decodeBase64())
    }

    @Test
    fun testPost() = runTest {
        testHelper("post", HttpClient::post)

        HttpClient().use { client ->
            client.post("https://httpbin.org/post", "testing").also {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testPostStreaming() = runTest {
        HttpClient().use { client ->
            val file = testDir / "file"
            file.write("Test")

            client.post("https://httpbin.org/post", file.readStream()).also {
                assertContains(it.readBody(), "\"data\": \"Test\"")
            }
        }
    }

    @Test
    fun testPatch() = runTest {
        testHelper("patch", HttpClient::patch)

        HttpClient().use { client ->
            client.patch("https://httpbin.org/patch", "testing").also {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testPut() = runTest {
        testHelper("put", HttpClient::put)

        HttpClient().use { client ->
            client.put("https://httpbin.org/put", "testing").also {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testBasicAuth() = runTest {
        HttpClient {
            handler(BasicAuthHandler("test", "test"))
        }.use { client ->
            assertEquals(200, client.get("https://httpbin.org/basic-auth/test/test").statusCode)
        }
    }

    @Test
    fun testBearerAuth() = runTest {
        HttpClient {
            handler(BearerAuthHandler("test"))
        }.use { client ->
            assertEquals(200, client.get("https://httpbin.org/bearer").statusCode)
        }
    }

    @Test
    fun testPATAuth() = runTest {
        HttpClient {
        }.use { client ->
            assertEquals(
                200,
                client.get("https://httpbin.org/basic-auth/PAT/test", PersonalAccessTokenAuthHandler("test")).statusCode
            )
        }
    }
}
