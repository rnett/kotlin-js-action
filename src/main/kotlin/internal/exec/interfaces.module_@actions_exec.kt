@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/exec")
@file:JsNonModule

package internal.exec

import Buffer
import kotlin.js.*
import stream.internal.Writable

public external interface EnvBuilder {
    @nativeGetter
    public operator fun get(key: String): String?
    @nativeSetter
    public operator fun set(key: String, value: String)
}

public external interface ExecListeners {
    public var stdout: ((data: Buffer) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    public var stderr: ((data: Buffer) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    public var stdline: ((data: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    public var errline: ((data: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    public var debug: ((data: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

public external interface ExecOptions {
    public var cwd: String?
        get() = definedExternally
        set(value) = definedExternally
    public var env: EnvBuilder?
        get() = definedExternally
        set(value) = definedExternally
    public var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var outStream: Writable?
        get() = definedExternally
        set(value) = definedExternally
    public var errStream: Writable?
        get() = definedExternally
        set(value) = definedExternally
    public var windowsVerbatimArguments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var failOnStdErr: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var ignoreReturnCode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    public var delay: Number?
        get() = definedExternally
        set(value) = definedExternally
    public var input: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    public var listeners: ExecListeners?
        get() = definedExternally
        set(value) = definedExternally
}