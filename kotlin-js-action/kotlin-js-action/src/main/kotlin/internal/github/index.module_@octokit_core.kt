@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@octokit/core")
@file:JsNonModule

package internal.github

import internal.HookCollection
import kotlin.js.*

internal external interface `T$9` {
    var plugins: Array<Any>
}

internal external interface `T$10` {
    var debug: (message: String, additionalInfo: Any?) -> Any
    var info: (message: String, additionalInfo: Any?) -> Any
    var warn: (message: String, additionalInfo: Any?) -> Any
    var error: (message: String, additionalInfo: Any?) -> Any
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

internal external open class Octokit(options: OctokitOptions = definedExternally) {
    open var request: Any
    open var graphql: Any
    open var log: `T$10`
    open var hook: HookCollection
    open var auth: (args: Any) -> Promise<Any>
    @nativeGetter
    open operator fun get(key: String): Any?
    @nativeSetter
    open operator fun set(key: String, value: Any)

    companion object {
        var VERSION: String
        fun <S : Constructor<Any>> defaults(self: S, defaults: OctokitOptions): Any /* Any & S */
        fun <S : Constructor<Any>> defaults(self: S, defaults: Function<*>): Any /* Any & S */
        var plugins: Array<OctokitPlugin>
        fun <S : Constructor<Any>, T : Array<OctokitPlugin>> plugin(self: S, vararg newPlugins: T): `T$9` /* `T$9` & S & Constructor<UnionToIntersection<ReturnTypeOf<T>>> */
    }
}