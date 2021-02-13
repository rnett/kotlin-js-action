package com.rnett.action.core

import com.rnett.action.LazyValProvider
import kotlin.reflect.KProperty

internal class InputDelegate(val name: String?) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getRequiredInput(name ?: property.name)
    }
}

internal class OptionalInputDelegate(val name: String?) : LazyValProvider<String?> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getOptionalInput(name ?: property.name)
    }
}

internal class OptionalInputDelegateWithDefault(val name: String?, val default: () -> String) : LazyValProvider<String> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
        core.getOptionalInput(name ?: property.name) ?: default()
    }
}

public object inputs : LazyValProvider<String> by InputDelegate(null) {

    public operator fun invoke(name: String): LazyValProvider<String> = InputDelegate(name)

    public val optional: LazyValProvider<String?> = OptionalInputDelegate(null)

    public fun optional(name: String): LazyValProvider<String?> = OptionalInputDelegate(name)

    public fun withDefault(default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(null, default)

    public fun withDefault(name: String, default: () -> String): LazyValProvider<String> =
        OptionalInputDelegateWithDefault(name, default)

    public operator fun get(name: String): String? = core.getOptionalInput(name)
    public fun getRequired(name: String): String = core.getRequiredInput(name)
    public fun getOrElse(name: String, default: () -> String): String = get(name) ?: default()
}