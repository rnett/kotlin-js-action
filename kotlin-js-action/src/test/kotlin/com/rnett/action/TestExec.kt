package com.rnett.action

import Buffer
import com.rnett.action.exec.exec
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
class TestExec : TestWithDir() {
    override fun Path.initDir() {
        descendant("testFile3").touch().write("Testing file")
    }

    val diff = if (OperatingSystem.isWindows) "\r\n" else ""

    @Test
    fun testExec() = GlobalScope.promise {
        exec.execCommand("javac", "--version", cwd = testDir)
    }

    @Test
    fun testExecAndCapture() = GlobalScope.promise {
        assertTrue(exec.execCommandAndCapture("javac", "--version", cwd = testDir).stdout.startsWith("javac"))
    }

    @Test
    fun testExecShell() = GlobalScope.promise {
        exec.execShell("cp testFile3 testOut1", cwd = testDir)
        val file = (testDir / "testOut1")
        assertEquals("Testing file", file.readText())
    }

    @Test
    fun testExecAndCaptureShell() = GlobalScope.promise {
        assertEquals("Testing file$diff", exec.execShellAndCapture("cat testFile3 | cat", cwd = testDir).stdout)
    }

    @Test
    fun testInputRedirect() = GlobalScope.promise {
        val output = exec.execShellAndCapture("cat", input = Buffer.from("Test"))
        assertEquals("Test", output.stdout)
    }

    @Test
    fun testOutputRedirect() = GlobalScope.promise {
        val outputFile = (testDir / "testOut2")
        val stream = outputFile.writeStream()
        exec.execShell("echo \"Test\"", outStream = stream)
        stream.close()
        assertEquals("Test", outputFile.readText().lines().drop(1).first())
    }
}