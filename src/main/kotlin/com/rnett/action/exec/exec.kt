package com.rnett.action.exec

import Buffer
import com.rnett.action.JsObject
import com.rnett.action.Path
import com.rnett.action.currentProcess
import internal.exec.ExecOptions
import kotlinx.coroutines.await

/**
 * Wrappers for [`@actions/exec`](https://github.com/actions/toolkit/tree/main/packages/exec).
 */
public object exec {
    private suspend fun execCommand(
        command: String,
        args: List<String> = emptyList(),
        options: ExecOptions? = null
    ): Int {
        val promise = if (options == null) {
            internal.exec.exec(command, args.toTypedArray())
        } else {
            internal.exec.exec(command, args.toTypedArray(), options)
        }
        return promise.await().toInt()
    }


    /**
     * Execute a command.  Uses shell execution, so output redirection etc is supported.
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
     * @return the exit code
     */
    public suspend fun execCommand(
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
        return execCommand(command, args, options = JsObject {
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
}