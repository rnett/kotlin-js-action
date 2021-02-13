package com.rnett.action.artifact

import com.rnett.action.JsObject
import com.rnett.action.Path
import internal.artifact.DownloadResponse
import internal.artifact.UploadResponse
import kotlinx.coroutines.await

public val UploadResponse.hasFailedItems: Boolean get() = failedItems.isNotEmpty()

public fun UploadResponse.requireSuccess() {
    if (hasFailedItems)
        error("Upload had failed items: $failedItems")
}

public object artifact {
    private val client by lazy { internal.artifact.create() }

    public suspend fun uploadArtifact(
        name: String, files: List<String>, rootDirectory: String, continueOnError: Boolean = true,
        retentionDays: Int? = null
    ): UploadResponse {
        return client.uploadArtifact(name, files.toTypedArray(), rootDirectory, JsObject {
            if (retentionDays != null)
                this.retentionDays = retentionDays
            this.continueOnError = continueOnError
        }).await()
    }

    public suspend fun uploadArtifact(
        name: String, files: List<Path>, rootDirectory: Path, continueOnError: Boolean = true,
        retentionDays: Int? = null
    ): UploadResponse = uploadArtifact(name, files.map { it.path }, rootDirectory.path, continueOnError, retentionDays)

    public suspend fun downloadArtifact(
        name: String,
        path: String? = null,
        createArtifactFolder: Boolean = false
    ): DownloadResponse {
        return if (path == null) {
            client.downloadArtifact(name, options = JsObject {
                this.createArtifactFolder = createArtifactFolder
            }).await()
        } else {
            client.downloadArtifact(name, path, JsObject {
                this.createArtifactFolder = createArtifactFolder
            }).await()
        }
    }

    public suspend fun downloadAllArtifacts(path: String? = null): List<DownloadResponse> {
        return if (path == null) {
            client.downloadAllArtifacts().await().toList()
        } else {
            client.downloadAllArtifacts(path).await().toList()
        }
    }

    public suspend fun downloadArtifact(
        name: String,
        path: Path? = null,
        createArtifactFolder: Boolean = false
    ): DownloadResponse = downloadArtifact(name, path?.path, createArtifactFolder)

    public suspend fun downloadAllArtifacts(path: Path? = null): List<DownloadResponse> =
        downloadAllArtifacts(path?.path)

}