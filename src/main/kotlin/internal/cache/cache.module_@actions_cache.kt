@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/cache")
@file:JsNonModule

package internal.cache

import kotlin.js.*


internal external fun restoreCache(paths: Array<String>, primaryKey: String, restoreKeys: Array<String> = definedExternally, options: DownloadOptions = definedExternally): Promise<String?>

internal external fun saveCache(paths: Array<String>, key: String, options: UploadOptions = definedExternally): Promise<Number>