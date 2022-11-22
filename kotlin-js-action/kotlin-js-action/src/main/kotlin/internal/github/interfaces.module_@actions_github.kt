@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/github")
@file:JsNonModule

package internal.github

import kotlin.js.*

internal external interface `T$13` {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
    var login: String
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface PayloadRepository {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
    var full_name: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String
    var owner: `T$13`
    var html_url: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface `T$14` {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
    var number: Number
    var html_url: String?
        get() = definedExternally
        set(value) = definedExternally
    var body: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface `T$15` {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
    var type: String
}

internal external interface `T$16` {
    var id: Number
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

internal external interface WebhookPayload {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
    var repository: PayloadRepository?
        get() = definedExternally
        set(value) = definedExternally
    var issue: `T$14`?
        get() = definedExternally
        set(value) = definedExternally
    var pull_request: `T$14`?
        get() = definedExternally
        set(value) = definedExternally
    var sender: `T$15`?
        get() = definedExternally
        set(value) = definedExternally
    var action: String?
        get() = definedExternally
        set(value) = definedExternally
    var installation: `T$16`?
        get() = definedExternally
        set(value) = definedExternally
    var comment: `T$16`?
        get() = definedExternally
        set(value) = definedExternally
}