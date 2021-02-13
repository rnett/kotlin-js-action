package com.rnett.action.core

/**
 * A setter to set outputs.
 */
public object outputs {

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