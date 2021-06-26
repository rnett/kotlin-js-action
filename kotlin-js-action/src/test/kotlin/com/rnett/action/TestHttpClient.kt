package com.rnett.action

import com.rnett.action.httpclient.BasicAuthHandler
import com.rnett.action.httpclient.BearerAuthHandler
import com.rnett.action.httpclient.HeaderProvider
import com.rnett.action.httpclient.HttpClient
import com.rnett.action.httpclient.HttpResponse
import com.rnett.action.httpclient.PersonalAccessTokenAuthHandler
import com.rnett.action.httpclient.decodeBase64
import com.rnett.action.httpclient.encodeBase64
import com.rnett.action.httpclient.use
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
class TestHttpClient : TestWithDir() {

    private suspend fun testHelper(
        code: Int,
        method: suspend (String, HeaderProvider) -> HttpResponse,
        json: Boolean = false,
        checkBody: Boolean = true,
        addJsonHeader: Boolean = false
    ): HttpResponse {
        val url = "https://httpstat.us/$code"
        val response = method(url) {
            if (addJsonHeader)
                this["accept"] = "application/json"
        }

        assertEquals(code, response.statusCode)

        if (!checkBody)
            return response

        val body = response.readBody()
        if (json) {
            assertTrue(body.startsWith("{\"code\": $code, "), "Code not in JSON response body: $body")
        }

        return response
    }

    suspend fun testHelper(
        method: suspend HttpClient.(String, HeaderProvider) -> HttpResponse,
        checkBody: Boolean = true
    ) {

        fun HttpClient.method(): suspend (String, HeaderProvider) -> HttpResponse =
            { url, headers -> this.method(url, headers) }

        HttpClient().use { client ->
            testHelper(200, client.method(), checkBody = checkBody)
            testHelper(404, client.method(), checkBody = checkBody)
            testHelper(505, client.method(), checkBody = checkBody)

            testHelper(200, client.method(), true, checkBody = checkBody, addJsonHeader = true)
        }

        HttpClient {
            header("Accept", "application/json")
        }.use { client ->
            testHelper(200, client.method(), true, checkBody = checkBody)
        }
    }

    suspend fun testHelper(
        method: suspend HttpClient.(String, String, HeaderProvider) -> HttpResponse,
        checkBody: Boolean = true
    ) {
        testHelper({ url, headers -> method(url, "", headers) }, checkBody)
    }

    @Test
    fun testGet() = GlobalScope.promise { testHelper(HttpClient::get) }

    @Test
    fun testUserAgent() = GlobalScope.promise {
        HttpClient {
            userAgent = "test"
        }.use { client ->
            client.get("https://httpbin.org/user-agent").let {
                assertContains(it.readBody(), "\"user-agent\": \"test\"")
            }
        }
    }

    @Test
    fun testHeaders() = GlobalScope.promise {
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
    fun testOptions() = GlobalScope.promise { testHelper(HttpClient::options) }

    @Test
    fun testDel() = GlobalScope.promise { testHelper(HttpClient::del) }

    @Test
    fun testHead() = GlobalScope.promise { testHelper(HttpClient::head, false) }

    @Test
    fun testBase64() {
        val encoded = "dGVzdDp0ZXN0"
        val decoded = "test:test"
        assertEquals(encoded, decoded.encodeBase64())
        assertEquals(decoded, encoded.decodeBase64())
    }

    @Test
    fun testPost() = GlobalScope.promise {
        testHelper(HttpClient::post)

        HttpClient().use { client ->
            client.post("https://httpbin.org/post", "testing").let {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testPostStreaming() = GlobalScope.promise {
        HttpClient().use { client ->
            val file = testDir / "file"
            file.write("Test")

            client.post("https://httpbin.org/post", file.readStream()).let {
                assertContains(it.readBody(), "\"data\": \"Test\"")
            }
        }
    }

    @Test
    fun testPatch() = GlobalScope.promise {
        testHelper(HttpClient::patch)

        HttpClient().use { client ->
            client.patch("https://httpbin.org/patch", "testing").let {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testPut() = GlobalScope.promise {
        testHelper(HttpClient::put)

        HttpClient().use { client ->
            client.put("https://httpbin.org/put", "testing").let {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testBasicAuth() = GlobalScope.promise {
        HttpClient {
            handler(BasicAuthHandler("test", "test"))
        }.use { client ->
            assertEquals(200, client.get("https://httpbin.org/basic-auth/test/test").statusCode)
        }
    }

    @Test
    fun testBearerAuth() = GlobalScope.promise {
        HttpClient {
            handler(BearerAuthHandler("test"))
        }.use { client ->
            assertEquals(200, client.get("https://httpbin.org/bearer").statusCode)
        }
    }

    @Test
    fun testPATAuth() = GlobalScope.promise {
        HttpClient {
        }.use { client ->
            assertEquals(
                200,
                client.get("https://httpbin.org/basic-auth/PAT/test", PersonalAccessTokenAuthHandler("test")).statusCode
            )
        }
    }
}