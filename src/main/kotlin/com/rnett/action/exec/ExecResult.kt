package com.rnett.action.exec

import Buffer
import com.rnett.action.Path
import com.rnett.action.currentProcess

public data class ExecResult(val returnCode: Int, val stdout: String, val stderr: String)

public suspend fun execAndCapture(
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
    debugListener: ((data: String) -> Unit)? = null,
    encoding: String = "utf8"
): ExecResult {
    val stdout = StringBuilder()
    val stderr = StringBuilder()
    val returnCode = exec(
        command,
        args,
        cwd,
        env,
        input,
        silent,
        outStream,
        errStream,
        windowsVerbatimArguments,
        failOnStdErr,
        ignoreReturnCode,
        delay,
        {
            stdout.append(it.toString(encoding))
            stdoutListener?.invoke(it)
        },
        {
            stderr.append(it.toString(encoding))
            stderrListener?.invoke(it)
        },
        stdoutLineListener,
        stderrLineListener,
        debugListener
    )
    return ExecResult(returnCode, stdout.toString(), stderr.toString())
}