@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

package internal

import kotlin.js.Promise

internal external interface AsyncGenerator<T, TReturn, TNext> : AsyncIterator<T, TReturn, TNext> {
    override fun next(vararg args: Any /* JsTuple<> | JsTuple<TNext> */): Promise<dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */>
    fun `return`(value: TReturn): Promise<dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */>
    fun `return`(value: Promise<TReturn>): Promise<dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */>
    override var `throw`: (e: Any) -> Promise<dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */>
}

internal external interface AsyncGenerator__2<T, TReturn> : AsyncGenerator<T, TReturn, Any>