package com.rnett.action.core

import com.rnett.action.LazyValProvider
import com.rnett.action.camelToSnakeCase
import kotlin.reflect.KProperty

internal class InputDelegate(val name: String?) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getRequiredInput(name ?: property.name.camelToSnakeCase())
    }
}

internal class OptionalInputDelegate(val name: String?) : LazyValProvider<String?> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getOptionalInput(name ?: property.name.camelToSnakeCase())
    }
}

internal class OptionalInputDelegateWithDefault(val name: String?, val default: () -> String) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getOptionalInput(name ?: property.name.camelToSnakeCase()) ?: default()
    }
}

/**
 * Accessors for input variables.
 *
 * Can be delegated from.  Delegated values are lazy.  Property names will be converted to snake-case unless name is specified.
 */
public object inputs : LazyValProvider<String> by InputDelegate(null) {

    /**
     * Get a delegate for [name].
     */
    public operator fun invoke(name: String): LazyValProvider<String> = InputDelegate(name)

    /**
     * Get an optional delegate.  Property names will be converted to snake-case unless name is specified.
     */
    public val optional: LazyValProvider<String?> = OptionalInputDelegate(null)

    /**
     * Get an optional delegate for [name].
     */
    public fun optional(name: String): LazyValProvider<String?> = OptionalInputDelegate(name)

    /**
     * Get a delegate with default.  Property names will be converted to snake-case unless name is specified.
     */
    public fun withDefault(default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(null, default)

    /**
     * Get a delegate with default for [name].
     */
    public fun withDefault(name: String, default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(name, default)

    /**
     * Get the input passed for [name].  Treats it as required, see [getRequired].
     */
    public operator fun get(name: String): String = getRequired(name)

    /**
     * Get the input passed for [name], or throws an error if it was not passed.
     */
    @Deprecated("Use operator", ReplaceWith("[name]"))
    public fun getRequired(name: String): String = core.getRequiredInput(name)

    /**
     * Get the input passed for [name], or throws an error if it was not passed.
     */
    public fun getOptional(name: String): String? = core.getOptionalInput(name)

    /**
     * Get the input passed for [name], or [default] if it was not passed.
     */
    public fun getOrElse(name: String, default: () -> String): String = get(name) ?: default()
}