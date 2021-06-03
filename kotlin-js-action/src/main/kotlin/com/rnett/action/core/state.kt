package com.rnett.action.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Accessors for state.  State is action-specific, and only useful when set in one phase and read in another (i.e. post).
 */
public object state {
    /**
     * Get state [name], returning `null` if it is not set.
     */
    public operator fun get(name: String): String? = core.getState(name)

    /**
     * Get state [name], throwing if it is not set.
     */
    public fun getRequired(name: String): String = core.getRequiredState(name)

    /**
     * Set state [name] to [value].
     */
    public operator fun set(name: String, value: String): Unit = core.saveState(name, value)

    public class StateDelegate(public val name: String): ReadWriteProperty<Any?, String>{
        override inline fun getValue(thisRef: Any?, property: KProperty<*>): String = state.getRequired(name)

        override inline fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            state[name] = value
        }
    }

    public class OptionalStateDelegate(public val name: String): ReadWriteProperty<Any?, String?>{
        override inline fun getValue(thisRef: Any?, property: KProperty<*>): String? = state.get(name)

        override inline fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if(value != null) {
                state[name] = value
            }
        }
    }

    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): StateDelegate = StateDelegate(property.name)
    public object optional{
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): OptionalStateDelegate = OptionalStateDelegate(property.name)
    }
}