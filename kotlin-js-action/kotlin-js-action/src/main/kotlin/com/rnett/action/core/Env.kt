package com.rnett.action.core

import com.rnett.action.currentProcess
import com.rnett.action.delegates.MutableDelegatable
import com.rnett.action.delegates.ifNull
import kotlinx.js.delete
import kotlinx.js.get
import kotlinx.js.set
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Environment variable accessors, that by default exports if [defaultExport] is `true`.
 *
 * Can be delegated from.
 * Delegating from [Environment] treats the input as optional.
 */
public abstract class Environment(private val defaultExport: Boolean) : MutableDelegatable(),
    ReadWriteProperty<Any?, String?> {

    /**
     * Get [name] from the environment, or throw if it is not present.
     */
    public override fun getRequired(name: String): String = get(name) ?: error("No environment variable $name")

    /**
     * Get [name] from the environment, or `null` if it is not present.
     */
    override fun getOptional(name: String): String? = currentProcess.env[name]

    /**
     * Get [name] from the environment, or `null` if it is not present.
     */
    public operator fun get(name: String): String? = getOptional(name)

    /**
     * Get [name] from the environment, or set [default] for [name] and return it.
     *
     * Follows the [Environment]'s export setting by default.
     */
    public fun getOrPut(name: String, export: Boolean, default: () -> String): String =
        get(name) ?: default().also {
            set(name, export, it)
        }

    /**
     * Set [name] in the environment, following the default export setting for the [Environment].
     */
    public override operator fun set(name: String, value: String) {
        set(name, defaultExport, value)
    }

    /**
     * Set [name] in the environment, following the default export setting for the [Environment].
     */
    public operator fun set(name: String, value: String?) {
        set(name, defaultExport, value)
    }

    /**
     * Set [name] in the environment, exporting according to [export].
     */
    public operator fun set(name: String, export: Boolean, value: String?) {
        if (export && value != null)
            core.exportVariable(name, value)
        if (value != null) {
            currentProcess.env[name] = value
        } else {
            delete(currentProcess.env[name])
        }
    }

    /**
     * Remove [name] from the environment.  Does not affect exports.
     */
    public override fun remove(name: String) {
        delete(currentProcess.env[name])
    }

    /**
     * Export [name] with [value].  Alias for `set(name, true, value)`.
     */
    public fun export(name: String, value: String) {
        set(name, true, value)
    }

    /**
     * Export [name] with it's current value.  Does not export if it isn't set.
     */
    public fun export(name: String) {
        val value = this[name]
        if (value != null) {
            export(name, value)
        }
    }

    private val selfDelegate by lazy { optionalDelegate(null) }

    /**
     * A delegate based on the property name, for an optional state.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return selfDelegate.getValue(thisRef, property)
    }

    /**
     * A delegate based on the property name, for an optional state.
     */
    public override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        selfDelegate.setValue(thisRef, property, value)
    }

    /**
     * Delegate for [name].  Follows the the [Environment]'s export setting.
     */
    public operator fun invoke(name: String): ReadWriteProperty<Any?, String?> = optionalDelegate(name)

    /**
     * Optional delegate.  Follows the the [Environment]'s export setting.
     */
    public val required: ReadWriteProperty<Any?, String> by lazy { delegate(null) }

    /**
     * Optional delegate for [name].  Follows the the [Environment]'s export setting.
     */
    public fun required(name: String): ReadWriteProperty<Any?, String> = delegate(name)

    /**
     * Get an optional delegate with a default value.
     */
    public fun withDefault(default: () -> String): ReadOnlyProperty<Any?, String> = inputs.optional.ifNull(default)

    /**
     * Get an optional delegate with a default value for [name].
     */
    public fun withDefault(name: String, default: () -> String): ReadOnlyProperty<Any?, String> = inputs.optional(
        name
    ).ifNull(default)
}

/**
 * Environment accessors, where set variables are exported for other steps and tasks.
 *
 * Export setting can be overridden in most methods, but will export by default.
 */
public object exportEnv : Environment(true) {
    /**
     * Get [env]
     */
    public val local: env = env
}

/**
 * Environment accessors, where set variables are **not** exported (i.e. not available in other steps and tasks).
 *
 * Export setting can be overridden in most methods, but will not export by default.
 */
public object env : Environment(false) {
    /**
     * Get [exportEnv]
     */
    public val export: exportEnv = exportEnv
}