package com.rnett.action.core

import com.rnett.action.LazyValProvider
import kotlin.reflect.KProperty

internal class InputDelegate(val name: String?) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        inputs.getRequired(name ?: property.name)
    }
}

internal class OptionalInputDelegate(val name: String?) : LazyValProvider<String?> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        inputs[name ?: property.name]
    }
}

internal class OptionalInputDelegateWithDefault(val name: String?, val default: () -> String) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        inputs[name ?: property.name] ?: default()
    }
}

/**
 * Accessors for input variables.
 *
 * Can be delegated from.  Delegated values are lazy.
 */
public object inputs : LazyValProvider<String> by InputDelegate(null) {

    /**
     * Get a delegate for [name].
     */
    public operator fun invoke(name: String): LazyValProvider<String> = InputDelegate(name)

    /**
     * Get an optional delegate.
     */
    public val optional: LazyValProvider<String?> = OptionalInputDelegate(null)

    /**
     * Get an optional delegate for [name].
     */
    public fun optional(name: String): LazyValProvider<String?> = OptionalInputDelegate(name)

    /**
     * Get a delegate with default.
     */
    public fun withDefault(default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(null, default)

    /**
     * Get a delegate with default for [name].
     */
    public fun withDefault(name: String, default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(name, default)

    /**
     * Get the input passed for [name].
     */
    public operator fun get(name: String): String? = inputs[name]

    /**
     * Get the input passed for [name], or throws an error if it was not passed.
     */
    public fun getRequired(name: String): String = getRequired(name)

    /**
     * Get the input passed for [name], or [default] if it was not passed.
     */
    public fun getOrElse(name: String, default: () -> String): String = get(name) ?: default()
}