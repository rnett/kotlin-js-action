package com.rnett.action.core

/**
 * Accessors for state.  State is action-specific, and only useful when set in one phase and read in another (i.e. post).
 */
public object state {
    /**
     * Get state [name], returning `null` if it is not set.
     */
    public operator fun get(name: String): String? = getRequired(name)

    /**
     * Get state [name], throwing if it is not set.
     */
    public fun getRequired(name: String): String = state[name]

    /**
     * Set state [name] to [value].
     */
    public operator fun set(name: String, value: String): Unit = state[name] = value
}