package com.rnett.action.exec

import Buffer
import com.rnett.action.Path
import com.rnett.action.currentProcess

public data class ExecResult(val returnCode: Int, val stdout: String, val stderr: String)


/**
 * Execute a command and capture `stdout` and `stderr`.  Uses shell execution, so output redirection etc is supported.
 *
 * @param command command to execute (can include additional args). Must be correctly escaped.
 * @param args optional arguments for tool. Escaping is handled by the lib.
 * @param cwd the working directory
 * @param env the environment.  Uses the current environment by default.
 * @param input input to write to the subprocess's stdin
 * @param silent whether to hide output
 * @param outStream the output stream to use.  Defaults to process.stdout.
 * @param errStream the error stream to use.  Defaults to process.stderr.
 * @param windowsVerbatimArguments whether to skip escaping arguments for Windows
 * @param failOnStdErr whether to fail if output is send to stderr
 * @param ignoreReturnCode whether to not fail the process if the subprocess fails.  Default throws an exception for non-0 return codes.
 * @param delay How long in ms to wait for STDIO streams to close after the exit event of the process before terminating
 * @param stdoutListener listener for stdout output
 * @param stderrListener listener for stderr output
 * @param stdoutLineListener listener for stdout output, called per line
 * @param stderrLineListener listener for stderr output, called per line
 * @param debugListener listener for debug output
 */
public suspend fun execCommandAndCapture(
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
    val returnCode = execCommand(
        command = command,
        args = args,
        cwd = cwd,
        env = env,
        input = input,
        silent = silent,
        outStream = outStream,
        errStream = errStream,
        windowsVerbatimArguments = windowsVerbatimArguments,
        failOnStdErr = failOnStdErr,
        ignoreReturnCode = ignoreReturnCode,
        delay = delay,
        stdoutListener = {
            stdout.append(it.toString(encoding))
            stdoutListener?.invoke(it)
        },
        stderrListener = {
            stderr.append(it.toString(encoding))
            stderrListener?.invoke(it)
        },
        stdoutLineListener = stdoutLineListener,
        stderrLineListener = stderrLineListener,
        debugListener = debugListener
    )
    return ExecResult(returnCode, stdout.toString(), stderr.toString())
}