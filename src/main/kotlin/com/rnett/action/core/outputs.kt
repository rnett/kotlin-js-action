package com.rnett.action.core

import com.rnett.action.AnyVarProperty
import com.rnett.action.camelToSnakeCase
import kotlin.reflect.KProperty

internal class OutputDelegate(val name: String?): AnyVarProperty<String> {
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        outputs.set(name ?: property.name.camelToSnakeCase(), value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return ""
    }
}

/**
 * A setter to set outputs.
 *
 * Can be delegated from.  Property names will be converted to snake-case unless name is specified.  Delegates only support set, `""` is always returned from get.
 */
public object outputs: AnyVarProperty<String> by OutputDelegate(null) {

    /**
     * Get a delegate for [name].
     *
     * Delegates only support set, `""` is always returned from get.
     */
    public operator fun invoke(name: String): AnyVarProperty<String> = OutputDelegate(name)

    /**
     * Set an output [name] to [value].
     */
    public operator fun set(name: String, value: String) {
        core.setOutput(name, value)
    }


    /**
     * Set all outputs from [outputs].
     */
    public fun setAll(outputs: Map<String, String>) {
        outputs.forEach { (k, v) -> set(k, v) }
    }
}