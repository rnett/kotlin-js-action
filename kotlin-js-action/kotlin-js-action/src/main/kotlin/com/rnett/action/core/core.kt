package com.rnett.action.core

import NodeJS.get
import com.rnett.action.JsObject
import com.rnett.action.currentProcess
import com.rnett.action.writeLine
import fs.`T$45`
import kotlinx.coroutines.await
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Wrappers for [`@actions/core`](https://github.com/actions/toolkit/tree/main/packages/core).
 * Most are deprecated in favor of the specialized extensions.
 */
public object core {

    /**
     * Get an OIDC ID token from the GitHub OIDC provider.
     *
     * This gets a JWT ID token which would help to get access token from third party cloud providers
     */
    public suspend fun getOIDCIDToken(audience: String? = null): String = internal.core.getIDToken(audience).await()

    /**
     * Issue a GitHub Action command.  Generally should not be used, most commands are wrapped.
     *
     * For example, `::set-output name={name}::{value}` would be `issueCommand("set-output", value, "name" to name)` (or better yet `outputs[name] = value`).
     */
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

    /**
     * Issue a GitHub Action command.  Generally should not be used, most commands are wrapped.
     *
     * For example, `::set-output name={name}::{value}` would be `issueCommand("set-output", value, "name" to name)` (or better yet `outputs[name] = value`).
     */
    public fun issueCommand(command: String, value: String, vararg properties: Pair<String, String>): Unit =
        issueCommand(command, value, properties.toMap())

    /**
     * Issue a GitHub Action file command.  Generally should not be used, most commands are wrapped.
     */
    public fun issueFileCommand(command: String, message: String) {
        val filePath =
            currentProcess.env["GITHUB_$command"]
                ?: kotlin.error("Unable to find environment variable for file command $command")
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

    @Deprecated("Use exportEnv", ReplaceWith("exportEnv[name] = value", "com.rnett.action.core.exportEnv"))
    public fun exportVariable(name: String, value: String): Unit = internal.core.exportVariable(name, value)

    public fun exportVariableStringify(name: String, value: Any): Unit = internal.core.exportVariable(name, value)

    @Deprecated("Use maskSecret", ReplaceWith("maskSecret(secret)", "com.rnett.action.core.maskSecret"))
    public fun setSecret(secret: String) {
        internal.core.setSecret(secret)
    }

    @Deprecated("Use PATH", ReplaceWith("PATH += inputPath", "com.rnett.action.core.PATH"))
    public fun addPath(inputPath: String): Unit = internal.core.addPath(inputPath)

    /**
     * Gets the input, or throws [IllegalStateException].
     */
    @Deprecated("Use inputs", ReplaceWith("inputs.getRequired(name)", "com.rnett.action.core.inputs"))
    public fun getRequiredInput(name: String, trimWhitespace: Boolean = true): String {
        return getOptionalInput(name, trimWhitespace) ?: kotlin.error("No input found for $name")
    }

    @Deprecated("Use inputs", ReplaceWith("inputs.getOptional(name)", "com.rnett.action.core.inputs"))
    public fun getOptionalInput(name: String, trimWhitespace: Boolean = true): String? {
        return try {
            internal.core.getInput(name, JsObject {
                this.required = true
                this.trimWhitespace = trimWhitespace
            })
        } catch (error: Throwable) {
            if (error.message == "Input required and not supplied: $name")
                return null
            else
                throw error
        }
    }

    @Deprecated("Use outputs", ReplaceWith("outputs[name] = value", "com.rnett.action.core.outputs"))
    public fun setOutput(name: String, value: String): Unit = internal.core.setOutput(name, value)
    public fun setOutputStringify(name: String, value: Any): Unit = internal.core.setOutput(name, value)

    @Deprecated("Use log.echoCommands", ReplaceWith("log.echoCommands = enabled", "com.rnett.action.core.log"))
    public fun setCommandEcho(enabled: Boolean): Unit = internal.core.setCommandEcho(enabled)

    @Deprecated("Use log.echoCommands", ReplaceWith("log.echoCommands = enabled", "com.rnett.action.core.log"))
    public var echoCommands: Boolean?
        get() = null
        set(value) {
            setCommandEcho(value ?: false)
        }

    @Deprecated("Use log.fatal or fail", ReplaceWith("log.fatal(message)", "com.rnett.action.core.log"))
    public fun setFailed(message: String): Unit = internal.core.setFailed(message)

    @Deprecated("Use log.fatal or fail", ReplaceWith("log.fatal(exception)", "com.rnett.action.core.log"))
    public fun setFailed(exception: Throwable): Unit = internal.core.setFailed(exception)

    @Deprecated("Use log.isDebug", ReplaceWith("log.isDebug", "com.rnett.action.core.log"))
    public val isDebug: Boolean
        get() = internal.core.isDebug()

    @Deprecated("Use log.debug", ReplaceWith("log.fatal(message)", "com.rnett.action.core.log"))
    public fun debug(message: String): Unit = internal.core.debug(message)

    @Deprecated("Use log.info", ReplaceWith("log.fatal(message)", "com.rnett.action.core.log"))
    public fun info(message: String): Unit = internal.core.info(message)

    @Deprecated("Use log.error", ReplaceWith("log.error(message)", "com.rnett.action.core.log"))
    public fun error(message: String): Unit = internal.core.error(message)

    @Deprecated("Use log.error", ReplaceWith("log.error(exception)", "com.rnett.action.core.log"))
    public fun error(exception: Throwable): Unit = internal.core.error(exception)

    @Deprecated("Use log.warning", ReplaceWith("log.warning(message)", "com.rnett.action.core.log"))
    public fun warning(message: String): Unit = internal.core.warning(message)

    @Deprecated("Use log.warning", ReplaceWith("log.warning(exception)", "com.rnett.action.core.log"))
    public fun warning(exception: Throwable): Unit = internal.core.warning(exception)

    @Deprecated("Use log.notice", ReplaceWith("log.notice(message)", "com.rnett.action.core.log"))
    public fun notice(message: String): Unit = internal.core.notice(message)

    @Deprecated("Use log.notice", ReplaceWith("log.notice(exception)", "com.rnett.action.core.log"))
    public fun notice(exception: Throwable): Unit = internal.core.notice(exception)

    @Deprecated("Use log.error", ReplaceWith("log.error(message)", "com.rnett.action.core.log"))
    public fun error(message: String, annotationProperties: AnnotationProperties): Unit =
        internal.core.error(message, annotationProperties.toJsObject())

    @Deprecated("Use log.error", ReplaceWith("log.error(exception)", "com.rnett.action.core.log"))
    public fun error(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        internal.core.error(exception, annotationProperties.toJsObject())

    @Deprecated("Use log.warning", ReplaceWith("log.warning(message)", "com.rnett.action.core.log"))
    public fun warning(message: String, annotationProperties: AnnotationProperties): Unit =
        internal.core.warning(message, annotationProperties.toJsObject())

    @Deprecated("Use log.warning", ReplaceWith("log.warning(exception)", "com.rnett.action.core.log"))
    public fun warning(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        internal.core.warning(exception, annotationProperties.toJsObject())

    @Deprecated("Use log.notice", ReplaceWith("log.notice(message)", "com.rnett.action.core.log"))
    public fun notice(message: String, annotationProperties: AnnotationProperties): Unit =
        internal.core.notice(message, annotationProperties.toJsObject())

    @Deprecated("Use log.notice", ReplaceWith("log.notice(exception)", "com.rnett.action.core.log"))
    public fun notice(exception: Throwable, annotationProperties: AnnotationProperties): Unit =
        internal.core.notice(exception, annotationProperties.toJsObject())

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use log.withGroup")
    public fun startGroup(name: String): Unit = internal.core.startGroup(name)

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use log.withGroup")
    public fun endGroup(): Unit = internal.core.endGroup()

    @Deprecated("Use log.withGroup", ReplaceWith("log.withGroup(name, block)", "com.rnett.action.core.log"))
    public inline fun <R> withGroup(name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        startGroup(name)
        val result = block()
        endGroup()
        return result
    }

    @Deprecated("Use state", ReplaceWith("state[name] = value", "com.rnett.action.core.state"))
    public fun saveState(name: String, value: String): Unit = internal.core.saveState(name, value)

    public fun saveStateStringify(name: String, value: Any): Unit = internal.core.saveState(name, value)

    @Deprecated("Use state", ReplaceWith("state[name]", "com.rnett.action.core.state"))
    public fun getState(name: String): String? = currentProcess.env["STATE_$name"]

    @Deprecated("Use state", ReplaceWith("state.getRequired(name)", "com.rnett.action.core.state"))
    public fun getRequiredState(name: String): String = getState(name) ?: kotlin.error("No state value for $name")
}