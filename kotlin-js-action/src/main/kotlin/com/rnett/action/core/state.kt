package com.rnett.action.core

import com.rnett.action.delegates.MutableDelegatable
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Accessors for state.  State is action-specific, and only useful when set in one phase and read in another (i.e. post).
 * Delegating from [state] treats the input as required.
 */
public object state : MutableDelegatable(), ReadWriteProperty<Any?, String> {
    /**
     * Get state [name], returning `null` if it is not set.
     */
    public operator fun get(name: String): String? = getOptional(name)

    /**
     * Get state [name], throwing if it is not set.
     */
    public override fun getRequired(name: String): String = core.getRequiredState(name)

    /**
     * Get state [name], returning `null` if it is not set.
     */
    override fun getOptional(name: String): String? = core.getState(name)

    /**
     * Set state [name] to [value].
     */
    public override operator fun set(name: String, value: String): Unit = core.saveState(name, value)

    /**
     * A delegate based on the property name, for a required state.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return getRequired(property.name.delegateName())
    }

    /**
     * A delegate based on the property name, for a required state.
     */
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        set(property.name.delegateName(), value)
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