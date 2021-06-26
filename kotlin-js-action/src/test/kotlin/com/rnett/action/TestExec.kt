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
        descendant("testFile3").touch().writeSync("Testing file")
    }

    val diff = if (OperatingSystem.isWindows) "\r\n" else ""

    @Test
    fun testExec() = GlobalScope.promise {
        exec.execCommand("javac", "--version")
    }

    @Test
    fun testExecAndCapture() = GlobalScope.promise {
        assertTrue(exec.execCommandAndCapture("javac", "--version").stdout.startsWith("javac"))
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
        if (!OperatingSystem.isWindows) {
            val output = exec.execShellAndCapture("cat", input = Buffer.from("Test"))
            assertEquals("Test", output.throwIfFailure().stdout)
        }
    }

    @Test
    fun testOutputRedirect() = GlobalScope.promise {
        val outputFile = (testDir / "testOut3")
        val stream = outputFile.writeStream()
        exec.execShell("echo \"Test\"", outStream = stream)
        stream.close()
        assertEquals("Test", outputFile.readText().lines()[1])
    }

    @Test
    fun testOutputInShellRedirect() = GlobalScope.promise {
        val outputFile = (testDir / "testOut2")
        exec.execShell("echo \"Test\" > \"$outputFile\"")
        assertEquals("Test$diff", outputFile.readText(if (OperatingSystem.isWindows) "ucs2" else "utf8").let {
            if (OperatingSystem.isWindows)
                it.substring(1)
            else
                it
        })
    }
}