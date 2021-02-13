package com.rnett.action.artifact

import com.rnett.action.JsObject
import com.rnett.action.Path
import kotlinx.coroutines.await

/**
 * True if some items in the upload failed.
 */
public val UploadResponse.hasFailedItems: Boolean get() = failedItems.isNotEmpty()

/**
 * Throws an error if the upload has failed items.
 */
public fun UploadResponse.requireSuccess() {
    if (hasFailedItems)
        error("Upload had failed items: $failedItems")
}

/**
 * The full Path to where the artifact was downloaded
 */
public val DownloadResponse.downloadLocation: Path get() = Path(downloadPath)

/**
 * Wrappers for [`@actions/artifact`](https://github.com/actions/toolkit/tree/main/packages/artifact).  Creates and uses one client.
 */
public object artifact {
    private val client by lazy { internal.artifact.create() }

    /**
     * Uploads an artifact
     *
     * @param name the name of the artifact, required
     * @param files a list of absolute or relative paths that denote what files should be uploaded
     * @param rootDirectory an absolute or relative file path that denotes the root parent directory of the files being uploaded
     * @param continueOnError whether the upload should continue if there is an error in a part.  Failed parts will be reflected in the [UploadResponse].
     * @param retentionDays How long to hold the artifact.  If `null` (default), defaults to the repository setting.  Can be between 1 and the repository max (which by default is 90)
     * @returns single UploadInfo object
     */
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


    /**
     * Uploads an artifact
     *
     * @param name the name of the artifact, required
     * @param files a list of absolute or relative paths that denote what files should be uploaded
     * @param rootDirectory an absolute or relative file path that denotes the root parent directory of the files being uploaded
     * @param continueOnError whether the upload should continue if there is an error in a part.  Failed parts will be reflected in the [UploadResponse].
     * @param retentionDays How long to hold the artifact.  If `null` (default), defaults to the repository setting.  Can be between 1 and the repository max (which by default is 90)
     * @returns single [UploadResponse] object
     */
    public suspend fun uploadArtifact(
        name: String, files: List<Path>, rootDirectory: Path, continueOnError: Boolean = true,
        retentionDays: Int? = null
    ): UploadResponse = uploadArtifact(name, files.map { it.path }, rootDirectory.path, continueOnError, retentionDays)

    /**
     * Downloads a single artifact associated with a run.  Will error if it fails.
     *
     * @param name the name of the artifact being downloaded
     * @param path optional path that denotes where the artifact will be downloaded to
     * @param createArtifactFolder whether to create a folder for the artifact in [path].  Otherwise downloads the contents into [path].
     */
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

    /**
     * Downloads all artifacts associated with a run.  Will error if it fails.
     * Because there are multiple artifacts being downloaded, a folder will be created for each one in the specified or default directory
     * @param path optional path that denotes where the artifacts will be downloaded to
     */
    public suspend fun downloadAllArtifacts(path: String? = null): List<DownloadResponse> {
        return if (path == null) {
            client.downloadAllArtifacts().await().toList()
        } else {
            client.downloadAllArtifacts(path).await().toList()
        }
    }

    /**
     * Downloads a single artifact associated with a run
     *
     * @param name the name of the artifact being downloaded
     * @param path optional path that denotes where the artifact will be downloaded to
     * @param createArtifactFolder whether to create a folder for the artifact in [path].  Otherwise downloads the contents into [path].
     */
    public suspend fun downloadArtifact(
        name: String,
        path: Path? = null,
        createArtifactFolder: Boolean = false
    ): DownloadResponse = downloadArtifact(name, path?.path, createArtifactFolder)

    /**
     * Downloads all artifacts associated with a run. Because there are multiple artifacts being downloaded, a folder will be created for each one in the specified or default directory
     * @param path optional path that denotes where the artifacts will be downloaded to
     */
    public suspend fun downloadAllArtifacts(path: Path? = null): List<DownloadResponse> =
        downloadAllArtifacts(path?.path)

}