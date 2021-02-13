@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/artifact")
@file:JsNonModule

package internal.artifact

import kotlin.js.*

internal external interface ArtifactClient {
    fun uploadArtifact(name: String, files: Array<String>, rootDirectory: String, options: UploadOptions = definedExternally): Promise<UploadResponse>
    fun downloadArtifact(name: String, path: String = definedExternally, options: DownloadOptions = definedExternally): Promise<DownloadResponse>
    fun downloadAllArtifacts(path: String = definedExternally): Promise<Array<DownloadResponse>>
}

internal external fun create(): ArtifactClient