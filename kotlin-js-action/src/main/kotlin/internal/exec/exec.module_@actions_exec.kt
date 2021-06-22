@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/exec")
@file:JsNonModule

package internal.exec

import kotlin.js.Promise

internal external fun exec(
    commandLine: String,
    args: Array<String> = definedExternally,
    options: ExecOptions = definedExternally
): Promise<Number>

internal external fun getExecOutput(
    commandLine: String,
    args: Array<String> = definedExternally,
    options: ExecOptions = definedExternally
): Promise<ExecOutput>