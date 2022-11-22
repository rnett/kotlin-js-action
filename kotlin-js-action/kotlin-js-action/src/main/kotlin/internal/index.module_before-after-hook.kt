@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("before-after-hook")
@file:JsNonModule

package internal

import kotlin.js.*

internal external interface HookCollection {
    @nativeInvoke
    operator fun invoke(name: String, hookMethod: HookMethod<Any, Any>, options: Any = definedExternally): Promise<Any>
    @nativeInvoke
    operator fun invoke(name: String, hookMethod: HookMethod<Any, Any>): Promise<Any>
    @nativeInvoke
    operator fun invoke(name: Array<String>, hookMethod: HookMethod<Any, Any>, options: Any = definedExternally): Promise<Any>
    @nativeInvoke
    operator fun invoke(name: Array<String>, hookMethod: HookMethod<Any, Any>): Promise<Any>
    fun before(name: String, beforeHook: BeforeHook<Any>)
    fun error(name: String, errorHook: ErrorHook<Any, Any>)
    fun after(name: String, afterHook: AfterHook<Any, Any>)
    fun wrap(name: String, wrapHook: WrapHook<Any, Any>)
    fun remove(name: String, hook: BeforeHook<Any>)
    fun remove(name: String, hook: ErrorHook<Any, Any>)
    fun remove(name: String, hook: WrapHook<Any, Any>)
}