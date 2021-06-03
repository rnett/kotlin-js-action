@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/artifact")
@file:JsNonModule

package com.rnett.action.artifact

/**
 * The response to an artifact upload.
 */
public external interface UploadResponse {
    /**
     * The name of the artifact that was uploaded
     */
    public val artifactName: String

    /**
     * A list of all items that are meant to be uploaded as part of the artifact
     */
    public val artifactItems: Array<String>

    /**
     * Total size of the artifact in bytes that was uploaded
     */
    public val size: Number

    /**
     * A list of items that were not uploaded as part of the artifact (includes queued items that were not uploaded if
     * continueOnError is set to false). This is a subset of artifactItems.
     */
    public val failedItems: Array<String>
}

/**
 * The response of an artifact download.
 */
public external interface DownloadResponse {
    /**
     * The name of the artifact that was downloaded
     */
    public val artifactName: String

    /**
     * The full Path to where the artifact was downloaded
     */
    public val downloadPath: String
}