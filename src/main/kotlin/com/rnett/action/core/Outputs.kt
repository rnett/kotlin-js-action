package com.rnett.action.core

public object outputs {
    public operator fun set(name: String, value: String){
        core.setOutput(name, value)
    }
}