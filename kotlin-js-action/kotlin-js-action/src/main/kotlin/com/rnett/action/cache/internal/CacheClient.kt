package com.rnett.action.cache.internal

import com.rnett.action.JsObject
import com.rnett.action.Path
import com.rnett.action.core.env
import com.rnett.action.core.maskSecret
import internal.httpclient.HttpClient
import internal.httpclient.HttpClientResponse
import internal.httpclient.RequestHandler
import internal.httpclient.RequestInfo
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.js.get
import kotlinx.js.set
import node.ReadableStream
import node.http.OutgoingHttpHeaders
import node.http.RequestOptions
import kotlin.js.Promise
import kotlin.math.min

private external fun encodeURIComponent(str: String): String

/**
 * Experimental reverse engineered cache API, there are probably bugs.
 */
@MustBeDocumented
@RequiresOptIn("Experimental reverse engineered cache API, there are probably bugs.")
public annotation class ExperimentalCacheAPI

@ExperimentalCacheAPI
public external interface CacheEntry {
    public val cacheKey: String
    public val scope: String
    public val creationTime: String
    public val archiveLocation: String
    public val cacheVersion: String
}

@ExperimentalCacheAPI
private external interface ReserveRequest {
    var key: String
    var version: String
}

@ExperimentalCacheAPI
private external interface ReserveCacheResponse {
    val cacheId: Int
}

@ExperimentalCacheAPI
private external interface CommitCacheRequest {
    var size: Long
}

private external interface ReadStreamOptions {

    var autoClose: Boolean?
    var end: Long?
    var start: Long?
    var fd: Number?
}

/**
 * Cache accessors, similar to `@actions/cache`
 */
@ExperimentalCacheAPI
public class CacheClient(private val userAgent: String = "Kotlin/JS GitHub Action wrapper") {

    private fun Number.isSuccess() = this in 200..299

    @ExperimentalCacheAPI
    public fun getCacheApiUrl(resource: String): String {
        val baseUrl = env["ACTIONS_CACHE_URL"] ?: env["ACTIONS_RUNTIME_URL"] ?: error("Can't find cache service url")
        return baseUrl.replace("pipelines", "artifactcache") + "_apis/artifactcache/$resource"
    }

    private fun requestOptions(): internal.httpclient.RequestOptions = JsObject {
        headers = JsObject {
            this["Accept"] = "application/json;api-version=6.0-preview.1"
        }
    }

    private val httpClient: HttpClient = kotlin.run {
        val token = env["ACTIONS_RUNTIME_TOKEN"] ?: error("Runtime token not found")
        HttpClient(userAgent, arrayOf(object : RequestHandler {
            override fun prepareRequest(options: RequestOptions) {
                options.headers?.let {
                    it["Authorization"] = "Bearer " + token
                }
            }

            override fun canHandleAuthentication(response: HttpClientResponse): Boolean = false

            override fun handleAuthentication(
                httpClient: HttpClient,
                requestInfo: RequestInfo,
                data: Any?
            ): Promise<HttpClientResponse> = JsObject()

        }), requestOptions())
    }

    /**
     * Get a cache entry for the keys and version, or null if none exists
     */
    @ExperimentalCacheAPI
    public suspend fun getCacheEntry(keys: List<String>, version: String): CacheEntry? {
        val resource = "cache?keys=${encodeURIComponent(keys.joinToString(","))}&version=$version"
        val url = getCacheApiUrl(resource)
        val response = httpClient.getJson<CacheEntry>(url).await()
        if (response.statusCode == 204)
            return null

        if (!response.statusCode.isSuccess())
            error("Cache service responded with ${response.statusCode}")

        if (response.result?.archiveLocation.isNullOrBlank())
            error("Cache not found")

        val result = response.result!!

        maskSecret(result.archiveLocation)
        return result
    }

    /**
     * Get whether a cache exists for these keys and version
     */
    @ExperimentalCacheAPI
    public suspend fun hasCache(keys: List<String>, version: String): Boolean = getCacheEntry(keys, version) != null

    /**
     * Download a file
     */
    @ExperimentalCacheAPI
    public suspend fun downloadFile(url: String, destination: Path): Unit = coroutineScope {
        val client = HttpClient(userAgent)
        destination.parent.mkdir()

        val outStream = destination.writeStream()
        val response = client.get(url).await()

        launch {
            response.message.pipe(outStream)
        }.join()

        val contentLength = response.message.headers["content-length"] as String?
        contentLength?.toLongOrNull()?.let { expectedSize ->
            val realSize = destination.stats()!!.size
            if (expectedSize != realSize)
                error("Incomplete download, expected file size $expectedSize but only got $realSize")
        }
    }

    /**
     * Try to download a cache entry
     * @return true if the cache was found
     */
    @ExperimentalCacheAPI
    public suspend fun downloadCacheEntry(destination: Path, keys: List<String>, version: String): Boolean {
        val entry = getCacheEntry(keys, version) ?: return false
        downloadFile(entry.archiveLocation, destination)
        return true
    }

    /**
     * Read text from a url
     */
    @ExperimentalCacheAPI
    public suspend fun readUrl(url: String): String {
        val client = HttpClient(userAgent)
        return client.get(url).await().readBody().await()
    }

    /**
     * Read a cache entry
     * @return null if no cache was found
     */
    @ExperimentalCacheAPI
    public suspend fun readCacheEntry(keys: List<String>, version: String): String? =
        getCacheEntry(keys, version)?.let {
            readUrl(it.archiveLocation)
        }

    /**
     * Reserve a cache key
     * @return the cacheId if successfully reserved, or null otherwise
     */
    @ExperimentalCacheAPI
    public suspend fun reserveCache(key: String, version: String): Int? {
        val request = JsObject<ReserveRequest> {
            this.key = key
            this.version = version
        }
        val response = httpClient.postJson<ReserveCacheResponse>(getCacheApiUrl("caches"), request)
            .await()

        if (response.statusCode == 409)
            return null

        return response.result?.cacheId
    }

    /**
     * Upload a chunk to a reserved cache id.
     *
     * Unlike `@actions/cache`, [end] is exclusive
     */
    @ExperimentalCacheAPI
    public suspend fun uploadChunk(cacheId: Int, openStream: () -> ReadableStream, start: Long, end: Long) {
        val additionalHeaders = JsObject<OutgoingHttpHeaders> {
            this["Content-Type"] = "application/octet-stream"
            this["Content-Range"] = "bytes $start-${end - 1}/*"
        }
        val url = getCacheApiUrl("caches/$cacheId")
        val response = httpClient.sendStream(
            "PATCH",
            url,
            openStream(),
            additionalHeaders
        ).await()
        if (!response.message.statusCode!!.isSuccess())
            error(
                "Cache service responded with ${response.message.statusCode} during chunk upload: ${
                    response.readBody().await()
                }"
            )
    }

    /**
     * Upload text to a reserved cache id.
     */
    @ExperimentalCacheAPI
    public suspend fun uploadText(cacheId: Int, text: String) {
        val additionalHeaders = JsObject<OutgoingHttpHeaders> {
            this["Content-Type"] = "application/octet-stream"
            this["Content-Range"] = "bytes 0-${text.length - 1}/*"
        }
        val url = getCacheApiUrl("caches/$cacheId")
        val response = httpClient.patch(
            url,
            text,
            additionalHeaders
        ).await()
        if (!response.message.statusCode!!.isSuccess())
            error(
                "Cache service responded with ${response.message.statusCode} during text upload: ${
                    response.readBody().await()
                }"
            )
    }

    /**
     * Upload a file to a reserved cache id, using concurrency.
     */
    @ExperimentalCacheAPI
    public suspend fun uploadFile(
        cacheId: Int,
        file: Path,
        concurrency: Int = 4,
        maxChunkSize: Long = 32 * 1024 * 1024
    ): Unit = coroutineScope {
        if (!file.isFile())
            error("file to upload must be a file")

        val fileSize = file.stats()!!.size.toLong()

        val fd = node.fs.openSync(file.path, "r".asDynamic(), mode = "0o666".asDynamic())

        var offset: Long = 0
        val jobs = List(concurrency) {
            val chunkSize = min(fileSize - offset, maxChunkSize)
            val start = offset
            val end = offset + chunkSize
            offset += maxChunkSize

            launch {
                uploadChunk(cacheId, {
                    node.fs.createReadStream(file.path, JsObject<ReadStreamOptions> {
                        this.fd = fd
                        this.start = start
                        this.end = end
                        this.autoClose = false
                    })
                }, start, end)
            }
        }
        jobs.joinAll()
        node.fs.closeSync(fd)
    }

    /**
     * Commit a cache
     */
    @ExperimentalCacheAPI
    public suspend fun commitCache(cacheId: Int, dataSize: Long) {
        val request = JsObject<CommitCacheRequest> {
            this.size = dataSize
        }

        val result = httpClient.postJson<Any?>(getCacheApiUrl("caches/$cacheId"), request).await()
        if (!result.statusCode.isSuccess())
            error("Error commiting cache: ${result.statusCode}")
    }

    /**
     * Upload a file to the cache and then commit it
     */
    @ExperimentalCacheAPI
    public suspend fun saveFile(cacheId: Int, file: Path, concurrency: Int = 4, maxChunkSize: Long = 32 * 1024 * 1024) {
        uploadFile(cacheId, file, concurrency, maxChunkSize)
        val size = file.stats()!!.size.toLong()
        commitCache(cacheId, size)
    }

    /**
     * Upload text to the cache and then commit it
     */
    @ExperimentalCacheAPI
    public suspend fun saveText(cacheId: Int, text: String) {
        uploadText(cacheId, text)
        commitCache(cacheId, text.length.toLong())
    }
}