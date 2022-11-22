package com.rnett.action.cache

import com.rnett.action.JsObject
import com.rnett.action.Path
import kotlinx.coroutines.await

/**
 * Wrappers for [`@actions/cache`](https://github.com/actions/toolkit/tree/main/packages/cache).
 */
public object cache {

    /**
     * Restores cache from keys
     *
     * @param paths a list of file paths to restore from the cache
     * @param key an explicit key for restoring the cache
     * @param restoreKeys an optional ordered list of keys to use for restoring the cache if no cache hit occurred for [key]
     * @param useAzureSdk whether to use the Azure Blob SDK to download caches that are stored on Azure Blob Storage to improve reliability and performance
     * @param downloadConcurrency Number of parallel downloads (this option only applies when using the Azure SDK)
     * @param timeoutInMs Maximum time for each download request, in milliseconds (this option only applies when using the Azure SDK)
     * @returns string returns the key for the cache hit, otherwise returns null if none hit
     */
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


    /**
     * Restores cache from keys, throwing if the cache misses.
     *
     * @param paths a list of file paths to restore from the cache
     * @param key an explicit key for restoring the cache
     * @param restoreKeys an optional ordered list of keys to use for restoring the cache if no cache hit occurred for [key]
     * @param useAzureSdk whether to use the Azure Blob SDK to download caches that are stored on Azure Blob Storage to improve reliability and performance
     * @param downloadConcurrency Number of parallel downloads (this option only applies when using the Azure SDK)
     * @param timeoutInMs Maximum time for each download request, in milliseconds (this option only applies when using the Azure SDK)
     * @returns string returns the key for the cache hit, otherwise returns null if none hit
     */
    public suspend fun restoreRequiredCache(
        paths: List<String>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String {
        return restoreCache(paths, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)
            ?: error("No cache found for key $key, with fallback keys $restoreKeys")
    }


    /**
     * Restores cache from keys
     *
     * @param paths a list of file paths to restore from the cache
     * @param key an explicit key for restoring the cache
     * @param restoreKeys an optional ordered list of keys to use for restoring the cache if no cache hit occurred for [key]
     * @param useAzureSdk whether to use the Azure Blob SDK to download caches that are stored on Azure Blob Storage to improve reliability and performance
     * @param downloadConcurrency Number of parallel downloads (this option only applies when using the Azure SDK)
     * @param timeoutInMs Maximum time for each download request, in milliseconds (this option only applies when using the Azure SDK)
     * @returns string returns the key for the cache hit, otherwise returns null if none hit
     */
    public suspend fun restoreCache(
        paths: List<Path>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String? = restoreCache(paths.map { it.path }, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)


    /**
     * Restores cache from keys, throwing if the cache misses.
     *
     * @param paths a list of file paths to restore from the cache
     * @param key an explicit key for restoring the cache
     * @param restoreKeys an optional ordered list of keys to use for restoring the cache if no cache hit occurred for [key]
     * @param useAzureSdk whether to use the Azure Blob SDK to download caches that are stored on Azure Blob Storage to improve reliability and performance
     * @param downloadConcurrency Number of parallel downloads (this option only applies when using the Azure SDK)
     * @param timeoutInMs Maximum time for each download request, in milliseconds (this option only applies when using the Azure SDK)
     * @returns string returns the key for the cache hit, otherwise returns null if none hit
     */
    public suspend fun restoreRequiredCache(
        paths: List<Path>, key: String, restoreKeys: List<String> = emptyList(),
        useAzureSdk: Boolean = true,
        downloadConcurrency: Int = 8,
        timeoutInMs: Int = 30000
    ): String =
        restoreRequiredCache(paths.map { it.path }, key, restoreKeys, useAzureSdk, downloadConcurrency, timeoutInMs)

    /**
     * Saves a list of files with the specified key
     *
     * @param paths a list of file paths to be cached
     * @param key an explicit key for restoring the cache
     * @param uploadConcurrency  Number of parallel cache uploads
     * @param uploadChunkSize Maximum chunk size in bytes for cache upload
     * @returns number returns cacheId if the cache was saved successfully and throws an error if save fails
     */
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