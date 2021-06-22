package com.rnett.action.core

import com.rnett.action.delegates.Delegatable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Accessors for input variables.
 *
 * Can be delegated from.  Property names will be converted to snake-case unless name is specified.
 * Delegating from [inputs] treats the input as required.
 */
public object inputs : Delegatable(true), ReadOnlyProperty<Any?, String> {

    /**
     * Get the input passed for [name], or throws an error if it was not passed.
     */
    @Deprecated("Use operator", ReplaceWith("this[name]"))
    public override fun getRequired(name: String): String = core.getRequiredInput(name)

    /**
     * Get the input passed for [name], or throws an error if it was not passed.
     */
    public override fun getOptional(name: String): String? = core.getOptionalInput(name)

    /**
     * Get the input passed for [name].  Treats it as optional, see [getOptional].
     */
    public operator fun get(name: String): String? = getOptional(name)

    /**
     * A delegate based on the property name converted to snake case, for a required input.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return getRequired(property.name.delegateName())
    }

    /**
     * Get a delegate for [name].
     */
    public operator fun invoke(name: String): ReadOnlyProperty<Any?, String> = delegate(name)

    /**
     * Get an optional delegate.  Property names will be converted to snake-case unless name is specified.
     */
    public val optional: ReadOnlyProperty<Any?, String?> by lazy { optionalDelegate(null) }

    /**
     * Get an optional delegate for [name].
     */
    public fun optional(name: String): ReadOnlyProperty<Any?, String?> = optionalDelegate(name)
}