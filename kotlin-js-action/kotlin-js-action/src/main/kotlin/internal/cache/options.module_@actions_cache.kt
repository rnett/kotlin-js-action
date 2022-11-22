@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/cache")
@file:JsNonModule

package internal.cache

import kotlin.js.*

internal external interface UploadOptions {
    var uploadConcurrency: Number?
        get() = definedExternally
        set(value) = definedExternally
    var uploadChunkSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface DownloadOptions {
    var useAzureSdk: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var downloadConcurrency: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timeoutInMs: Number?
        get() = definedExternally
        set(value) = definedExternally
    var segmentTimeoutInMs: Number?
        get() = definedExternally
        set(value) = definedExternally
}