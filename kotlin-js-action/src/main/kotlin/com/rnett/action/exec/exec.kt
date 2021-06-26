package com.rnett.action.exec

import Buffer
import com.rnett.action.JsObject
import com.rnett.action.OperatingSystem
import com.rnett.action.Path
import com.rnett.action.core.runOrFail
import internal.exec.ExecOptions
import internal.exec.ExecOutput
import kotlinx.coroutines.await

/**
 * Wrappers for [`@actions/exec`](https://github.com/actions/toolkit/tree/main/packages/exec).
 */
public object exec {
    private suspend fun execCommand(
        command: String,
        args: Array<out String>,
        options: ExecOptions? = null,
    ): Int = runOrFail {
        val promise = if (options == null) {
            internal.exec.exec(command, args as Array<String>)
        } else {
            internal.exec.exec(command, args as Array<String>, options)
        }
        return promise.await().toInt()
    }

    private suspend fun execCommandAndCapture(
        command: String,
        args: Array<out String>,
        options: ExecOptions? = null,
    ): ExecOutput = runOrFail {
        val promise = if (options == null) {
            internal.exec.getExecOutput(command, args as Array<String>)
        } else {
            internal.exec.getExecOutput(command, args as Array<String>, options)
        }
        return promise.await()
    }

    private fun execOptions(
        cwd: Path?,
        env: Map<String, String>?,
        input: Buffer?,
        silent: Boolean,
        outStream: stream.internal.Writable?,
        errStream: stream.internal.Writable?,
        windowsVerbatimArguments: Boolean,
        failOnStdErr: Boolean,
        ignoreReturnCode: Boolean,
        delay: Long,
        stdoutListener: ((data: Buffer) -> Unit)?,
        stderrListener: ((data: Buffer) -> Unit)?,
        stdoutLineListener: ((data: String) -> Unit)?,
        stderrLineListener: ((data: String) -> Unit)?,
        debugListener: ((data: String) -> Unit)?,
    ): ExecOptions = JsObject {
        this.cwd = cwd?.path
        this.env = env?.let {
            JsObject {
                it.forEach { (k, v) ->
                    this[k] = v
                }
            }
        }
        this.ignoreReturnCode = ignoreReturnCode
        this.silent = silent
        this.outStream = outStream
        this.errStream = errStream
        this.windowsVerbatimArguments = windowsVerbatimArguments
        this.failOnStdErr = failOnStdErr
        this.ignoreReturnCode = true
        this.delay = delay
        this.input = input
        listeners = JsObject {
            this.stdout = stdoutListener
            this.stderr = stderrListener
            stdline = stdoutLineListener
            errline = stderrLineListener
            debug = debugListener
        }
    }


    /**
     * Execute a command.
     *
     * Output redirection and pipes do not appear to be supported, but you can set [outStream] using [Path.readStream]
     * (see [actions/toolkit#346](https://github.com/actions/toolkit/issues/346).
     * However, this will write the command as the first line (see [actions/toolkit#649](https://github.com/actions/toolkit/issues/649)).
     *
     * To workaround, you can use [execShell].
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
        vararg args: String,
        cwd: Path? = null,
        env: Map<String, String>? = null,
        input: Buffer? = null,
        silent: Boolean = false,
        outStream: stream.internal.Writable? = null,
        errStream: stream.internal.Writable? = null,
        windowsVerbatimArguments: Boolean = false,
        failOnStdErr: Boolean = false,
        ignoreReturnCode: Boolean = false,
        delay: Long = 10000,
        stdoutListener: ((data: Buffer) -> Unit)? = null,
        stderrListener: ((data: Buffer) -> Unit)? = null,
        stdoutLineListener: ((data: String) -> Unit)? = null,
        stderrLineListener: ((data: String) -> Unit)? = null,
        debugListener: ((data: String) -> Unit)? = null,
    ): Int {
        return execCommand(
            command,
            args,
            execOptions(
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
                stdoutListener = stdoutListener,
                stderrListener = stderrListener,
                stdoutLineListener = stdoutLineListener,
                stderrLineListener = stderrLineListener,
                debugListener = debugListener
            )
        )
    }

    /**
     * The default shell for the current OS.  Powershell for windows, `/bin/bash` for linux or mac.
     */
    public val defaultShell: Shell
        get() = if (OperatingSystem.isWindows) {
            Shell.powershell
        } else {
            Shell.bash
        }

    /**
     * Execute a command using the given or default shell.
     * Pipes and redirection are supported.
     * Only quotes and backticks are escaped in [command].
     *
     * @param command command to execute (can include additional args). Must be correctly escaped.
     * @param shell the shell to use, [defaultShell] by default.
     * @param cwd the working directory
     * @param env the environment.  Uses the current environment by default.
     * @param input input to write to the subprocess's stdin
     * @param silent whether to hide output
     * @param outStream the output stream to use.  Defaults to process.stdout.
     * @param errStream the error stream to use.  Defaults to process.stderr.
     * @param failOnStdErr whether to fail if output is send to stderr
     * @param ignoreReturnCode whether to not fail the process if the subprocess fails.  Default throws an exception for non-0 return codes.
     * @param delay How long in ms to wait for STDIO streams to close after the exit event of the process before terminating
     * @param stdoutListener listener for stdout output
     * @param stderrListener listener for stderr output
     * @param stdoutLineListener listener for stdout output, called per line
     * @param stderrLineListener listener for stderr output, called per line
     * @param debugListener listener for debug output
     */
    public suspend fun execShell(
        command: String,
        shell: Shell = defaultShell,
        cwd: Path? = null,
        env: Map<String, String>? = null,
        input: Buffer? = null,
        silent: Boolean = false,
        outStream: stream.internal.Writable? = null,
        errStream: stream.internal.Writable? = null,
        failOnStdErr: Boolean = false,
        ignoreReturnCode: Boolean = false,
        delay: Long = 10000,
        stdoutListener: ((data: Buffer) -> Unit)? = null,
        stderrListener: ((data: Buffer) -> Unit)? = null,
        stdoutLineListener: ((data: String) -> Unit)? = null,
        stderrLineListener: ((data: String) -> Unit)? = null,
        debugListener: ((data: String) -> Unit)? = null,
    ) {
        execCommand(
            command = shell.shellCommand(command),
            args = shell.args(command),
            cwd = cwd,
            env = env,
            input = input,
            silent = silent,
            outStream = outStream,
            errStream = errStream,
            windowsVerbatimArguments = !shell.escapeWindows,
            failOnStdErr = failOnStdErr,
            ignoreReturnCode = ignoreReturnCode,
            delay = delay,
            stdoutListener = stdoutListener,
            stderrListener = stderrListener,
            stdoutLineListener = stdoutLineListener,
            stderrLineListener = stderrLineListener,
            debugListener = debugListener
        )
    }

    /**
     * Execute a command and capture `stdout` and `stderr`.
     *
     * Output redirection and pipes do not appear to be supported, but you can set [outStream] using [Path.readStream]
     * (see [actions/toolkit#359](https://github.com/actions/toolkit/issues/359).
     * However, this will write the command as the first line (see [actions/toolkit#649](https://github.com/actions/toolkit/issues/649)).
     *
     * To workaround, you can use [execShell].
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
     * @param ignoreReturnCode whether to not fail the process if the subprocess fails.  True by default.
     * @param delay How long in ms to wait for STDIO streams to close after the exit event of the process before terminating
     * @param stdoutListener listener for stdout output
     * @param stderrListener listener for stderr output
     * @param stdoutLineListener listener for stdout output, called per line
     * @param stderrLineListener listener for stderr output, called per line
     * @param debugListener listener for debug output
     */
    public suspend fun execCommandAndCapture(
        command: String,
        vararg args: String,
        cwd: Path? = null,
        env: Map<String, String>? = null,
        input: Buffer? = null,
        silent: Boolean = false,
        outStream: stream.internal.Writable? = null,
        errStream: stream.internal.Writable? = null,
        windowsVerbatimArguments: Boolean = false,
        failOnStdErr: Boolean = false,
        ignoreReturnCode: Boolean = true,
        delay: Long = 10000,
        stdoutListener: ((data: Buffer) -> Unit)? = null,
        stderrListener: ((data: Buffer) -> Unit)? = null,
        stdoutLineListener: ((data: String) -> Unit)? = null,
        stderrLineListener: ((data: String) -> Unit)? = null,
        debugListener: ((data: String) -> Unit)? = null
    ): ExecResult {
        val result = execCommandAndCapture(
            command,
            args,
            execOptions(
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
                stdoutListener = stdoutListener,
                stderrListener = stderrListener,
                stdoutLineListener = stdoutLineListener,
                stderrLineListener = stderrLineListener,
                debugListener = debugListener
            )
        )
        return ExecResult(command, result.exitCode.toInt(), result.stdout, result.stderr)
    }

    //TODO look at using stdin rather than escaping

    /**
     * Execute a command using the given or default shell and capture the output.
     * Pipes and redirection are supported.
     * Only quotes and backticks are escaped in [command].
     *
     * @param command command to execute (can include additional args). Must be correctly escaped.
     * @param shell the shell to use, [defaultShell] by default.
     * @param cwd the working directory
     * @param env the environment.  Uses the current environment by default.
     * @param input input to write to the subprocess's stdin
     * @param silent whether to hide output
     * @param outStream the output stream to use.  Defaults to process.stdout.
     * @param errStream the error stream to use.  Defaults to process.stderr.
     * @param failOnStdErr whether to fail if output is send to stderr
     * @param ignoreReturnCode whether to not fail the process if the subprocess fails.  True by default.
     * @param delay How long in ms to wait for STDIO streams to close after the exit event of the process before terminating
     * @param stdoutListener listener for stdout output
     * @param stderrListener listener for stderr output
     * @param stdoutLineListener listener for stdout output, called per line
     * @param stderrLineListener listener for stderr output, called per line
     * @param debugListener listener for debug output
     */
    public suspend fun execShellAndCapture(
        command: String,
        shell: Shell = defaultShell,
        cwd: Path? = null,
        env: Map<String, String>? = null,
        input: Buffer? = null,
        silent: Boolean = false,
        outStream: stream.internal.Writable? = null,
        errStream: stream.internal.Writable? = null,
        failOnStdErr: Boolean = false,
        ignoreReturnCode: Boolean = true,
        delay: Long = 10000,
        stdoutListener: ((data: Buffer) -> Unit)? = null,
        stderrListener: ((data: Buffer) -> Unit)? = null,
        stdoutLineListener: ((data: String) -> Unit)? = null,
        stderrLineListener: ((data: String) -> Unit)? = null,
        debugListener: ((data: String) -> Unit)? = null,
    ): ExecResult {
        return execCommandAndCapture(
            command = shell.shellCommand(command),
            args = shell.args(command),
            cwd = cwd,
            env = env,
            input = input,
            silent = silent,
            outStream = outStream,
            errStream = errStream,
            windowsVerbatimArguments = !shell.escapeWindows,
            failOnStdErr = failOnStdErr,
            ignoreReturnCode = ignoreReturnCode,
            delay = delay,
            stdoutListener = stdoutListener,
            stderrListener = stderrListener,
            stdoutLineListener = stdoutLineListener,
            stderrLineListener = stderrLineListener,
            debugListener = debugListener
        )
    }
}