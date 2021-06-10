@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/glob")
@file:JsNonModule

package internal.glob

import kotlin.js.Promise

internal external fun create(patterns: String, options: GlobOptions = definedExternally): Promise<Globber>

internal external fun hashFiles(patterns: String, options: HashFileOptions = definedExternally): Promise<String>