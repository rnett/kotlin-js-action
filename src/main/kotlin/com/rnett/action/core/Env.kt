package com.rnett.action.core

import NodeJS.get
import NodeJS.set
import com.rnett.action.AnyVarProperty
import com.rnett.action.currentProcess
import kotlin.reflect.KProperty

/**
 * Environment variable accessors, that by default exports if [defaultExport] is `true`.
 *
 * Can be delegated from.
 */
public abstract class Environment(private val defaultExport: Boolean): AnyVarProperty<String> {

    /**
     * Get [name] from the environment, or `null` if it is not present.
     */
    public operator fun get(name: String): String? = currentProcess.env[name]

    /**
     * Get [name] from the environment, or throw if it is not present.
     */
    public fun getRequired(name: String): String = get(name) ?: error("No environment variable $name")

    /**
     * Get [name] from the environment, or [default] if it is not present.
     */
    public fun getOrElse(name: String, default: () -> String): String = get(name) ?: default()

    /**
     * Get [name] from the environment, or set [default] for [name] and return it.
     *
     * Follows the [Environment]'s export setting by default.
     */
    public fun getOrPut(name: String, export: Boolean = defaultExport, default: () -> String): String =
        get(name) ?: default().also {
            set(name, export, it)
        }

    /**
     * Set [name] in the environment, following the default export setting for the [Environment].
     */
    public operator fun set(name: String, value: String) {
        set(name, defaultExport, value)
    }

    /**
     * Set [name] in the environment, exporting according to [export].
     */
    public operator fun set(name: String, export: Boolean, value: String) {
        if (export)
        else
            currentProcess.env[name] = value
    }

    /**
     * Remove [name] from the environment.  Does not affect exports.
     */
    public fun remove(name: String) {
        currentProcess.env[name] = null
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

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return getRequired(property.name)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        set(property.name, value)
    }

    internal inner class EnvironmentDelegate(val name: String?) : AnyVarProperty<String> {

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            set(name ?: property.name, value)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return getRequired(name ?: property.name)
        }
    }

    internal inner class OptionalEnvironmentDelegate(val name: String?) : AnyVarProperty<String?> {

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if(value != null)
                set(name ?: property.name, value)
            else
                remove(name ?: property.name)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return get(name ?: property.name)
        }
    }

    internal inner class OptionalEnvironmentDelegateWithDefault(
        val name: String?,
        val default: () -> String,
        val put: Boolean = false
    ) : AnyVarProperty<String> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            set(name ?: property.name, value)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return if (put)
                getOrPut(name ?: property.name, default = default)
            else
                getOrElse(name ?: property.name, default)
        }
    }

    /**
     * Delegate for [name].  Follows the the [Environment]'s export setting.
     */
    public operator fun invoke(name: String): AnyVarProperty<String> = EnvironmentDelegate(name)

    /**
     * Optional delegate.  Follows the the [Environment]'s export setting.
     */
    public val optional: AnyVarProperty<String?> = OptionalEnvironmentDelegate(null)

    /**
     * Optional delegate for [name].  Follows the the [Environment]'s export setting.
     */
    public fun optional(name: String): AnyVarProperty<String?> = OptionalEnvironmentDelegate(name)

    /**
     * Delegate with default.  Follows the the [Environment]'s export setting.
     */
    public fun withDefault(default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(null, default)

    /**
     * Delegate with default for [name].  Follows the the [Environment]'s export setting.
     */
    public fun withDefault(name: String, default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(name, default)

    /**
     * Delegate with default, that sets the default if needed.  Follows the the [Environment]'s export setting.
     */
    public fun withDefaultPut(default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(null, default, true)

    /**
     * Delegate with default for [name], that sets the default if needed.  Follows the the [Environment]'s export setting.
     */
    public fun withDefaultPut(name: String, default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(name, default, true)
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