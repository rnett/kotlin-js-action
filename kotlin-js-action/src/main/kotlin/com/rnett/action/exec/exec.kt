package com.rnett.action.exec

import Buffer
import com.rnett.action.*
import com.rnett.action.core.runOrFail
import internal.exec.ExecOptions
import kotlinx.coroutines.await

/**
 * Wrappers for [`@actions/exec`](https://github.com/actions/toolkit/tree/main/packages/exec).
 */
public object exec {
    private suspend fun execCommand(
        command: String,
        args: List<String> = emptyList(),
        options: ExecOptions? = null,
    ): Int = runOrFail {
        val promise = if (options == null) {
            internal.exec.exec(command, args.toTypedArray())
        } else {
            internal.exec.exec(command, args.toTypedArray(), options)
        }
        return promise.await().toInt()
    }


    /**
     * Execute a command.
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
        debugListener: ((data: String) -> Unit)? = null,
    ): Int {
        return execCommandAndCapture(
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
            stdoutListener,
            stderrListener,
            stdoutLineListener,
            stderrLineListener,
            debugListener
        ).returnCode
    }

    /**
     * The default shell for the current OS.  Powershell for windows, `/bin/bash` for linux or mac.
     */
    public val defaultShell: Shell
        get() = if (currentOS == OperatingSystem.Windows) {
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
    public suspend fun execShell(
        command: String,
        shell: Shell = defaultShell,
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
    ) {
        execCommand(
            shell.withCommand(command),
            emptyList(),
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
            stdoutListener,
            stderrListener,
            stdoutLineListener,
            stderrLineListener,
            debugListener
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
        encoding: String = "utf8",
    ): ExecResult {
        val stdout = StringBuilder()
        val stderr = StringBuilder()
        val returnCode = execCommand(command = command,
            args = args,
            options = JsObject {
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
                this.ignoreReturnCode = true
                this.delay = delay
                this.input = input
                listeners = JsObject {
                    this.stdout = { it: Buffer ->
                        stdout.append(it.toString(encoding))
                        stdoutListener?.invoke(it)
                    }
                    this.stderr = { it: Buffer ->
                        stderr.append(it.toString(encoding))
                        stderrListener?.invoke(it)
                    }
                    stdline = stdoutLineListener
                    errline = stderrLineListener
                    debug = debugListener
                }
            })
        return ExecResult(command, returnCode, stdout.toString(), stderr.toString()).also {
            if(!ignoreReturnCode)
                it.throwIfFailure()
        }
    }

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
    public suspend fun execShellAndCapture(
        command: String,
        shell: Shell = defaultShell,
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
    ): ExecResult {
        return execCommandAndCapture(
            shell.withCommand(command),
            emptyList(),
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
            stdoutListener,
            stderrListener,
            stdoutLineListener,
            stderrLineListener,
            debugListener
        )
    }
}