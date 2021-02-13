package com.rnett.action.cache

import com.rnett.action.JsObject
import com.rnett.action.Path
import kotlinx.coroutines.await

public object cache {
    public suspend fun restoreCache(
        paths: List<String>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String? {
        return internal.cache.restoreCache(paths.toTypedArray(), key, restoreKeys.toTypedArray(), JsObject {
            this.useAzureSdk = useAzureSdk
            this.downloadConcurrency = downloadConcurrency
            this.timeoutInMs = timeoutInMs
        }).await()
    }

    public suspend fun restoreRequiredCache(
        paths: List<String>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String {
        return restoreCache(paths, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)
            ?: error("No cache found for key $key, with fallback keys $restoreKeys")
    }

    public suspend fun restoreCache(
        paths: List<Path>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String? = restoreCache(paths.map { it.path }, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)

    public suspend fun restoreRequiredCache(
        paths: List<Path>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String =
        restoreRequiredCache(paths.map { it.path }, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)

    public suspend fun saveCache(
        paths: List<String>, key: String,
        uploadConcurrency: Int = 4,
        uploadChunkSize: Long = 32 * 1024 * 1024
    ): Int {
        return internal.cache.saveCache(paths.toTypedArray(), key, JsObject {
            this.uploadConcurrency = uploadConcurrency
            this.uploadChunkSize = uploadChunkSize
        }).await().toInt()
    }
}