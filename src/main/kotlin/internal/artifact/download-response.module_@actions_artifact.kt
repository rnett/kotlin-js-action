@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/artifact")
@file:JsNonModule

package internal.artifact

import kotlin.js.*

public external interface DownloadResponse {
    public val artifactName: String
    public val downloadPath: String
}