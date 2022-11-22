@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/io")
@file:JsNonModule

package internal.io

import kotlin.js.Promise

internal external interface CopyOptions {
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var force: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var copySourceDirectory: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface MoveOptions {
    var force: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

internal external fun cp(source: String, dest: String, options: CopyOptions = definedExternally): Promise<Unit>

internal external fun mv(source: String, dest: String, options: MoveOptions = definedExternally): Promise<Unit>

internal external fun rmRF(inputPath: String): Promise<Unit>

internal external fun mkdirP(fsPath: String): Promise<Unit>

internal external fun which(tool: String, check: Boolean = definedExternally): Promise<String>

internal external fun findInPath(tool: String): Promise<Array<String>>