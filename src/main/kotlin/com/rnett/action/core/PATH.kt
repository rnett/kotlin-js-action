package com.rnett.action.core

public object PATH {
    public operator fun plusAssign(inputPath: String){
        core.addPath(inputPath)
    }
}