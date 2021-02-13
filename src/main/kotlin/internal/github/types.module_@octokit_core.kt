@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@octokit/core")
@file:JsNonModule

package internal.github

import kotlin.js.*

internal external interface `T$11` {
    var debug: (message: String) -> Any
    var info: (message: String) -> Any
    var warn: (message: String) -> Any
    var error: (message: String) -> Any
}

internal external interface OctokitOptions {
    var authStrategy: Any?
        get() = definedExternally
        set(value) = definedExternally
    var auth: Any?
        get() = definedExternally
        set(value) = definedExternally
    var userAgent: String?
        get() = definedExternally
        set(value) = definedExternally
    var previews: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var baseUrl: String?
        get() = definedExternally
        set(value) = definedExternally
    var log: `T$11`?
        get() = definedExternally
        set(value) = definedExternally
    var request: RequestRequestOptions?
        get() = definedExternally
        set(value) = definedExternally
    var timeZone: String?
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(option: String): Any?
    @nativeSetter
    operator fun set(option: String, value: Any)
}