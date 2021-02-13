@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/artifact")
@file:JsNonModule

package internal.artifact

import kotlin.js.*

public external interface UploadResponse {
    public val artifactName: String
    public val artifactItems: Array<String>
    public val size: Number
    public val failedItems: Array<String>
}