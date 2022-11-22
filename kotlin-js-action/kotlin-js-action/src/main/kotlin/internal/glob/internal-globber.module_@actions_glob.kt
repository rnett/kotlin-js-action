@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/glob")
@file:JsNonModule

package internal.glob

import internal.AsyncGenerator__2
import kotlin.js.*

internal external interface Globber {
    fun getSearchPaths(): Array<String>
    fun glob(): Promise<Array<String>>
    fun globGenerator(): AsyncGenerator__2<String, Unit>
}