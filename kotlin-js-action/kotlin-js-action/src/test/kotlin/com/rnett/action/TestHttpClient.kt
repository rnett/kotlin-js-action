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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
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
            assertContains(body, "\\{\\s*\"code\"\\s*:\\s*$code,\\s*".toRegex(), "Code not in JSON response body: $body")
        }

        return response
    }

    private suspend fun testHelper(
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

    private suspend fun testHelper(
        method: suspend HttpClient.(String, String, HeaderProvider) -> HttpResponse,
        checkBody: Boolean = true
    ) {
        testHelper({ url, headers -> method(url, "", headers) }, checkBody)
    }

    @Test
    fun testGet() = runTest { testHelper(HttpClient::get) }

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
    fun testOptions() = runTest { testHelper(HttpClient::options) }

    @Test
    fun testDel() = runTest { testHelper(HttpClient::del) }

    @Test
    fun testHead() = runTest { testHelper(HttpClient::head, false) }

    @Test
    fun testBase64() {
        val encoded = "dGVzdDp0ZXN0"
        val decoded = "test:test"
        assertEquals(encoded, decoded.encodeBase64())
        assertEquals(decoded, encoded.decodeBase64())
    }

    @Test
    fun testPost() = runTest {
        testHelper(HttpClient::post)

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
        testHelper(HttpClient::patch)

        HttpClient().use { client ->
            client.patch("https://httpbin.org/patch", "testing").also {
                assertContains(it.readBody(), "\"data\": \"testing\"")
            }
        }
    }

    @Test
    fun testPut() = runTest {
        testHelper(HttpClient::put)

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
