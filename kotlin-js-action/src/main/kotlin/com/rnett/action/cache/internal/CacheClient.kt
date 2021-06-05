package com.rnett.action.cache.internal

import NodeJS.ReadableStream
import NodeJS.get
import NodeJS.set
import com.rnett.action.JsObject
import com.rnett.action.Path
import com.rnett.action.core.env
import com.rnett.action.core.maskSecret
import http.RequestOptions
import internal.httpclient.*
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.js.Promise
import kotlin.math.min

private external fun encodeURIComponent(str: String): String

public external interface CacheEntry {
    public val cacheKey: String
    public val scope: String
    public val creationTime: String
    public val archiveLocation: String
    public val cacheVersion: String
}

private external interface ReserveRequest {
    var key: String
    var version: String
}

private external interface ReserveCacheResponse {
    val cacheId: Int
}

private external interface CommitCacheRequest {
    var size: Long
}

/**
 * Cache accessors, similar to `@actions/cache`
 */
public class CacheClient(private val userAgent: String = "Kotlin/JS Github Action wrapper") {

    private fun Number.isSuccess() = this in 200..299

    public fun getCacheApiUrl(resource: String): String {
        val baseUrl = env["ACTIONS_CACHE_URL"] ?: env["ACTIONS_RUNTIME_URL"] ?: error("Can't find cache service url")
        return baseUrl.replace("pipelines", "artifactcache") + "_apis/artifactcache/$resource"
    }

    private fun requestOptions(): IRequestOptions = JsObject {
        headers = JsObject {
            this["Accept"] = "application/json;api-version=6.0-preview.1"
        }
    }

    private val httpClient: HttpClient = kotlin.run {
        val token = env["ACTIONS_RUNTIME_TOKEN"] ?: error("Runtime token not found")
        HttpClient(userAgent, arrayOf(object : IRequestHandler {
            override fun prepareRequest(options: RequestOptions) {
                options.headers?.let {
                    it["Authorization"] = "Bearer " + token
                }
            }

            override fun canHandleAuthentication(response: IHttpClientResponse): Boolean = false

            override fun handleAuthentication(
                httpClient: IHttpClient,
                requestInfo: IRequestInfo,
                objs: Any
            ): Promise<IHttpClientResponse> = JsObject()

        }), requestOptions())
    }

    /**
     * Get a cache entry for the keys and version, or null if none exists
     */
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
    public suspend fun hasCache(keys: List<String>, version: String): Boolean = getCacheEntry(keys, version) != null

    /**
     * Download a file
     */
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
            val realSize = destination.stats!!.size
            if (expectedSize != realSize)
                error("Incomplete download, expected file size $expectedSize but only got $realSize")
        }
    }

    /**
     * Try to download a cache entry
     * @return true if the cache was found
     */
    public suspend fun downloadCacheEntry(destination: Path, keys: List<String>, version: String): Boolean {
        val entry = getCacheEntry(keys, version) ?: return false
        downloadFile(entry.archiveLocation, destination)
        return true
    }

    /**
     * Read text from a url
     */
    public suspend fun readUrl(url: String): String {
        val client = HttpClient(userAgent)
        return client.get(url).await().readBody().await()
    }

    /**
     * Read a cache entry
     * @return null if no cache was found
     */
    public suspend fun readCacheEntry(keys: List<String>, version: String): String? =
        getCacheEntry(keys, version)?.let {
            readUrl(it.archiveLocation)
        }

    /**
     * Reserve a cache key
     * @return the cacheId if successfully reserved, or null otherwise
     */
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
    public suspend fun uploadChunk(cacheId: Int, openStream: () -> ReadableStream, start: Long, end: Long) {
        val additionalHeaders = JsObject<IHeaders> {
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
        if (!response.message.statusCode.isSuccess())
            error(
                "Cache service responded with ${response.message.statusCode} during chunk upload: ${
                    response.readBody().await()
                }"
            )
    }

    /**
     * Upload text to a reserved cache id.
     */
    public suspend fun uploadText(cacheId: Int, text: String) {
        val additionalHeaders = JsObject<IHeaders> {
            this["Content-Type"] = "application/octet-stream"
            this["Content-Range"] = "bytes 0-${text.length - 1}/*"
        }
        val url = getCacheApiUrl("caches/$cacheId")
        val response = httpClient.patch(
            url,
            text,
            additionalHeaders
        ).await()
        if (!response.message.statusCode.isSuccess())
            error(
                "Cache service responded with ${response.message.statusCode} during text upload: ${
                    response.readBody().await()
                }"
            )
    }

    /**
     * Upload a file to a reserved cache id, using concurrency.
     */
    public suspend fun uploadFile(
        cacheId: Int,
        file: Path,
        concurrency: Int = 4,
        maxChunkSize: Long = 32 * 1024 * 1024
    ): Unit = coroutineScope {
        if (!file.isFile)
            error("file to upload must be a file")

        val fileSize = file.stats!!.size.toLong()

        val fd = fs.openSync(file.path, "r", mode = "0o666")

        var offset: Long = 0
        val jobs = List(concurrency) {
            val chunkSize = min(fileSize - offset, maxChunkSize)
            val start = offset
            val end = offset + chunkSize
            offset += maxChunkSize

            launch {
                uploadChunk(cacheId, {
                    fs.createReadStream(file.path, JsObject<fs.`T$50`> {
                        this.fd = fd
                        this.start = start
                        this.end = end
                        this.autoClose = false
                    })
                }, start, end)
            }
        }
        jobs.joinAll()
        fs.closeSync(fd)
    }

    /**
     * Commit a cache
     */
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
    public suspend fun saveFile(cacheId: Int, file: Path, concurrency: Int = 4, maxChunkSize: Long = 32 * 1024 * 1024) {
        uploadFile(cacheId, file, concurrency, maxChunkSize)
        val size = file.stats!!.size.toLong()
        commitCache(cacheId, size)
    }

    /**
     * Upload text to the cache and then commit it
     */
    public suspend fun saveText(cacheId: Int, text: String) {
        uploadText(cacheId, text)
        commitCache(cacheId, text.length.toLong())
    }
}