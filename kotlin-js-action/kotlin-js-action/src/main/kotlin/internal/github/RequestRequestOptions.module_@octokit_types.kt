@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@octokit/types")
@file:JsNonModule

package internal.github

import node.http.Agent
import kotlin.js.*

internal external interface RequestRequestOptions {
    var agent: Agent?
        get() = definedExternally
        set(value) = definedExternally
    var fetch: Fetch?
        get() = definedExternally
        set(value) = definedExternally
    var signal: Signal?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(option: String): Any?
    @nativeSetter
    operator fun set(option: String, value: Any)
}