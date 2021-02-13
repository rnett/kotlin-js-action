package com.rnett.action.core

import NodeJS.get
import NodeJS.set
import com.rnett.action.AnyVarProperty
import com.rnett.action.currentProcess
import kotlin.reflect.KProperty

public abstract class Environment(private val defaultExport: Boolean): AnyVarProperty<String> {
    public operator fun get(name: String): String? = currentProcess.env[name]

    public fun getRequired(name: String): String = get(name) ?: error("No environment variable $name")

    public fun getOrElse(name: String, default: () -> String): String = get(name) ?: default()

    public fun getOrPut(name: String, export: Boolean = defaultExport, default: () -> String): String =
        get(name) ?: default().also {
            set(name, export, it)
        }

    public operator fun set(name: String, value: String) {
        set(name, defaultExport, value)
    }

    public operator fun set(name: String, export: Boolean, value: String) {
        if (export)
            core.exportVariable(name, value)
        else
            currentProcess.env[name] = value
    }

    public fun remove(name: String){
        currentProcess.env[name] = null
    }

    public fun export(name: String, value: String) {
        set(name, true, value)
    }

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

    public operator fun invoke(name: String): AnyVarProperty<String> = EnvironmentDelegate(name)

    public val optional: AnyVarProperty<String?> = OptionalEnvironmentDelegate(null)

    public fun optional(name: String): AnyVarProperty<String?> = OptionalEnvironmentDelegate(name)

    public fun withDefault(default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(null, default)

    public fun withDefault(name: String, default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(name, default)

    public fun withDefaultPut(default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(null, default, true)

    public fun withDefaultPut(name: String, default: () -> String): AnyVarProperty<String> =
        OptionalEnvironmentDelegateWithDefault(name, default, true)
}

public object exportEnv : Environment(true) {
    public val local: env = env
}

public object env : Environment(false) {
    public val export: exportEnv = exportEnv
}