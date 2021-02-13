package com.rnett.action.core

public object State {
    public operator fun get(name: String): String? = core.getState(name)
    public fun getRequired(name: String): String = core.getRequiredState(name)

    public operator fun set(name: String, value: String): Unit = core.saveState(name, value)
}