package com.rnett.action.exec

import Buffer
import com.rnett.action.JsObject
import com.rnett.action.Path
import com.rnett.action.currentProcess
import internal.exec.ExecOptions
import kotlinx.coroutines.await

public suspend fun exec(command: String, args: List<String> = emptyList(), options: ExecOptions? = null): Int {
    val promise = if (options == null) {
        internal.exec.exec(command, args.toTypedArray())
    } else {
        internal.exec.exec(command, args.toTypedArray(), options)
    }
    return promise.await().toInt()
}

public suspend fun exec(
    command: String,
    args: List<String> = emptyList(),
    cwd: Path = Path("."),
    env: Map<String, String>? = null,
    input: Buffer? = null,
    silent: Boolean = false,
    outStream: stream.internal.Writable = currentProcess.stdout.asDynamic() as stream.internal.Writable,
    errStream: stream.internal.Writable = currentProcess.stderr.asDynamic() as stream.internal.Writable,
    windowsVerbatimArguments: Boolean = false,
    failOnStdErr: Boolean = false,
    ignoreReturnCode: Boolean = false,
    delay: Long = 10000,
    stdoutListener: ((data: Buffer) -> Unit)? = null,
    stderrListener: ((data: Buffer) -> Unit)? = null,
    stdoutLineListener: ((data: String) -> Unit)? = null,
    stderrLineListener: ((data: String) -> Unit)? = null,
    debugListener: ((data: String) -> Unit)? = null
): Int {
    return exec(command, args, options = JsObject {
        this.cwd = cwd.path
        this.env = env?.let {
            JsObject {
                it.forEach { (k, v) ->
                    this[k] = v
                }
            }
        }
        this.silent = silent
        this.outStream = outStream
        this.errStream = errStream
        this.windowsVerbatimArguments = windowsVerbatimArguments
        this.failOnStdErr = failOnStdErr
        this.ignoreReturnCode = ignoreReturnCode
        this.delay = delay
        this.input = input
        this.listeners = JsObject {
            this.stdout = stdoutListener
            this.stderr = stderrListener
            this.stdline = stdoutLineListener
            this.errline = stderrLineListener
            this.debug = debugListener
        }
    })
}