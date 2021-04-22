package com.rnett.action.exec

import Buffer
import com.rnett.action.Path
import com.rnett.action.currentProcess

public data class ExecFailureException(val command: String, val returnCode: Int, val stderr: String): RuntimeException("Command failed with return code $returnCode and stderr: $stderr")

public data class ExecResult(private val command: String, val returnCode: Int, val stdout: String, val stderr: String){
    public fun throwIfFailure(): ExecResult = apply {
        if(returnCode != 0)
            throw ExecFailureException(command, returnCode, stderr)
    }
}

