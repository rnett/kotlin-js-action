@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/core")
@file:JsNonModule

package internal.core

import kotlin.js.Promise

internal external interface InputOptions {
    var required: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var trimWhitespace: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

internal external enum class ExitCode {
    Success /* = 0 */,
    Failure /* = 1 */
}

internal external interface AnnotationProperties {
    var title: String?
        get() = definedExternally
        set(value) = definedExternally
    var startLine: Number?
        get() = definedExternally
        set(value) = definedExternally
    var endLine: Number?
        get() = definedExternally
        set(value) = definedExternally
    var startColumn: Number?
        get() = definedExternally
        set(value) = definedExternally
    var endColumn: Number?
        get() = definedExternally
        set(value) = definedExternally
    var file: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external fun exportVariable(name: String, param_val: Any)

internal external fun setSecret(secret: String)

internal external fun addPath(inputPath: String)

internal external fun getInput(name: String, options: InputOptions = definedExternally): String

internal external fun getMultilineInput(name: String, options: InputOptions = definedExternally): Array<String>

internal external fun getBooleanInput(name: String, options: InputOptions = definedExternally): Boolean

internal external fun setOutput(name: String, value: Any)

internal external fun setCommandEcho(enabled: Boolean)

internal external fun setFailed(message: String)

internal external fun setFailed(message: Throwable)

internal external fun isDebug(): Boolean

internal external fun debug(message: String)

internal external fun error(message: String, properties: AnnotationProperties = definedExternally)

internal external fun error(message: String)

internal external fun error(message: Throwable, properties: AnnotationProperties = definedExternally)

internal external fun error(message: Throwable)

internal external fun warning(message: String, properties: AnnotationProperties = definedExternally)

internal external fun warning(message: String)

internal external fun warning(message: Throwable, properties: AnnotationProperties = definedExternally)

internal external fun warning(message: Throwable)

internal external fun notice(message: String, properties: AnnotationProperties = definedExternally)

internal external fun notice(message: String)

internal external fun notice(message: Throwable, properties: AnnotationProperties = definedExternally)

internal external fun notice(message: Throwable)

internal external fun info(message: String)

internal external fun startGroup(name: String)

internal external fun endGroup()

internal external fun <T> group(name: String, fn: () -> Promise<T>): Promise<T>

internal external fun saveState(name: String, value: Any)

internal external fun getState(name: String): String

internal external fun getIDToken(aud: String?): Promise<String>