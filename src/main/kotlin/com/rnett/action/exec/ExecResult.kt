package com.rnett.action.exec

import Buffer
import com.rnett.action.Path
import com.rnett.action.currentProcess

public data class ExecResult(val returnCode: Int, val stdout: String, val stderr: String)

