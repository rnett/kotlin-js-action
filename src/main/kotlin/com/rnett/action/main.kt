package com.rnett.action

import Buffer
import com.rnett.action.core.core
import com.rnett.action.core.env
import com.rnett.action.core.inputs
import com.rnett.action.core.outputs
import com.rnett.action.exec.execAndCapture
import internal.core.setOutput

public suspend fun main(): Unit = runAction{

    val projectDir = if("build" in Path.cwd) Path.cwd.ancestor(4) else Path.cwd

    val res = execAndCapture("./gradlew.bat --version",
        cwd = projectDir,
        silent = true
    )

    println("Result: $res")

    println("Done")
}