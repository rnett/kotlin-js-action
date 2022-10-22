package com.rnett.action

import Buffer
import com.rnett.action.exec.exec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TestExec : TestWithDir() {
    override fun Path.initDir() {
        descendant("testFile3").touch().writeSync("Testing file")
    }

    val diff = if (OperatingSystem.isWindows) "\r\n" else ""

    @Test
    fun testExec() = runTest {
        exec.execCommand("javac", "--version")
    }

    @Test
    fun testExecAndCapture() = runTest {
        assertTrue(exec.execCommandAndCapture("javac", "--version").stdout.startsWith("javac"))
    }

    @Test
    fun testExecShell() = runTest {
        exec.execShell("cp testFile3 testOut1", cwd = testDir)
        val file = (testDir / "testOut1")
        assertEquals("Testing file", file.readText())
    }

    @Test
    fun testExecAndCaptureShell() = runTest {
        assertEquals("Testing file$diff", exec.execShellAndCapture("cat testFile3 | cat", cwd = testDir).stdout)
    }

    @Test
    fun testInputRedirect() = runTest {
        if (!OperatingSystem.isWindows) {
            val output = exec.execShellAndCapture("cat", input = Buffer.from("Test"))
            assertEquals("Test", output.throwIfFailure().stdout)
        }
    }

    @Test
    fun testOutputRedirect() = runTest {
        val outputFile = (testDir / "testOut3")
        val stream = outputFile.writeStream()
        exec.execShell("echo \"Test\"", outStream = stream)
        stream.close()
        assertEquals("Test", outputFile.readText().lines()[1])
    }

    @Test
    fun testOutputInShellRedirect() = runTest {
        val shellDiff = if (OperatingSystem.isWindows) "\r\n" else "\n"

        val outputFile = (testDir / "testOut2")
        exec.execShell("echo \"Test\" > \"$outputFile\"")
        assertEquals("Test$shellDiff", outputFile.readText(if (OperatingSystem.isWindows) "ucs2" else "utf8").let {
            if (OperatingSystem.isWindows)
                it.substring(1)
            else
                it
        })
    }
}
