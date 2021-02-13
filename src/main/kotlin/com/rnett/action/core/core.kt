package com.rnett.action.core

import NodeJS.get
import com.rnett.action.JsObject
import com.rnett.action.currentProcess
import com.rnett.action.writeLine
import fs.`T$45`
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public object core {

    public fun issueCommand(command: String, value: String, properties: Map<String, String> = emptyMap()) {
        val cmd = buildString {
            append("::$command")
            if (properties.isNotEmpty()) {
                append(" ")
                append(properties.entries.joinToString { "${it.key}=${escapeProperty(it.value)}" })
            }
            append("::${escapeData(value)}")
        }
        currentProcess.stdout.writeLine(cmd)
    }

    public fun issueFileCommand(command: String, message: String) {
        val filePath =
            currentProcess.env["GITHUB_$command"] ?: kotlin.error("Unable to find environment variable for file command $command")
        if (!fs.existsSync(filePath)) {
            kotlin.error("Missing file at path: $filePath")
        }
        fs.appendFileSync(filePath, message + os.EOL, JsObject<`T$45`> { encoding = "utf8" })
    }

    private fun escapeData(data: String) = data
        .replace("%", "%25")
        .replace("\r", "%0D")
        .replace("\n", "%0A")

    private fun escapeProperty(property: String) = escapeData(property)
        .replace(":", "%3A")
        .replace(",", "%2C")

    public fun exportVariable(name: String, value: String): Unit = internal.core.exportVariable(name, value)

    public fun exportVariableStringify(name: String, value: Any): Unit = internal.core.exportVariable(name, value)

    public fun setSecret(secret: String) {
        internal.core.setSecret(secret)
    }

    public fun addPath(inputPath: String): Unit = internal.core.addPath(inputPath)

    public fun getInput(name: String, required: Boolean = false): String? {
        return try {
            internal.core.getInput(name, JsObject {
                this.required = true
            })
        } catch (error: Throwable) {
            if (required) {
                throw error
            } else {
                if (error.message == "Input required and not supplied: $name")
                    null
                else
                    throw error
            }
        }
    }

    /**
     * Gets the required input, or throws [IllegalStateException].
     */
    public fun getRequiredInput(name: String): String {
        return try {
            internal.core.getInput(name, JsObject {
                this.required = true
            })
        } catch (error: Throwable) {
            if (error.message == "Input required and not supplied: $name")
                throw IllegalStateException("No input found for $name", cause = error)
            else
                throw error
        }
    }

    public fun getOptionalInput(name: String): String? = getInput(name, false)

    public fun setOutput(name: String, value: String): Unit = internal.core.setOutput(name, value)
    public fun setOutputStringify(name: String, value: Any): Unit = internal.core.setOutput(name, value)

    public fun setCommandEcho(enabled: Boolean): Unit = internal.core.setCommandEcho(enabled)

    public var echoCommands: Boolean?
        get() = null
        set(value) {
            setCommandEcho(value ?: false)
        }

    public fun setFailed(message: String): Unit = internal.core.setFailed(message)
    public fun setFailed(exception: Throwable): Unit = internal.core.setFailed(exception)

    public val isDebug: Boolean get() = internal.core.isDebug()

    public fun debug(message: String): Unit = internal.core.debug(message)

    public fun info(message: String): Unit = internal.core.info(message)

    public fun error(message: String): Unit = internal.core.error(message)

    public fun error(exception: Throwable): Unit = internal.core.error(exception)

    public fun warning(message: String): Unit = internal.core.warning(message)

    public fun warning(exception: Throwable): Unit = internal.core.warning(exception)

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use withGroup")
    public fun startGroup(name: String): Unit = internal.core.startGroup(name)

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use withGroup")
    public fun endGroup(): Unit = internal.core.endGroup()

    public inline fun <R> withGroup(name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        startGroup(name)
        val result = block()
        endGroup()
        return result
    }

    public fun saveState(name: String, value: String): Unit = internal.core.saveState(name, value)

    public fun saveStateStringify(name: String, value: Any): Unit = internal.core.saveState(name, value)

    public fun getState(name: String): String? = currentProcess.env["STATE_$name"]

    public fun getRequiredState(name: String): String = getState(name) ?: kotlin.error("No state value for $name")
}