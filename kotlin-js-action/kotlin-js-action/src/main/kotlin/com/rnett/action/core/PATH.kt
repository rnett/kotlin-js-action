package com.rnett.action.core

import com.rnett.action.Path

/**
 * A setter for the path.
 */
public object PATH {
    /**
     * Add a new path [inputPath] to PATH.
     */
    public operator fun plusAssign(inputPath: String) {
        core.addPath(inputPath)
    }

    /**
     * Add a new path [inputPath] to PATH.
     */
    public operator fun plusAssign(inputPath: Path): Unit = plusAssign(inputPath.path)
}