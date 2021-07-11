package com.rnett.action

import com.rnett.action.core.isActionRunner
import com.rnett.action.toolcache.toolcache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(DelicateCoroutinesApi::class)
class TestToolCache : TestWithDir() {

    val licenseUrl = "https://raw.githubusercontent.com/rnett/kotlin-js-action/main/LICENSE"
    suspend fun localLicense() = (Path(TestEnv.testCwd) / "LICENSE").readText()

    @Test
    fun testDownload() = GlobalScope.promise {
        val resultFile = testDir / "testLicense"
        val result = toolcache.downloadTool(licenseUrl, resultFile)
        assertEquals(result, resultFile)
        assertEquals(localLicense(), resultFile.readText())

        if (isActionRunner) {
            val tempResult = toolcache.downloadTool(licenseUrl)
            assertEquals(localLicense(), tempResult.readText())
        }
    }

    @Test
    fun testExtract() = GlobalScope.promise {
        val archivesDir = Path(TestEnv.testCwd) / "kotlin-js-action/src/test/resources/archives"
        val extractedDir = testDir / "testExtract"
        archivesDir.children.forEach {

            if (it.name.endsWith(".7z") && !OperatingSystem.isWindows)
                return@forEach

            val extracted = toolcache.extract(it, extractedDir)
            assertEquals("Test file", (extracted / "test/file.txt").readText())
            extractedDir.delete(true)
        }
    }

    @Test
    fun testCacheDir() = GlobalScope.promise {
        if (!isActionRunner) return@promise

        val dir = testDir / "cacheDir"
        toolcache.downloadTool(licenseUrl, dir / "test")

        toolcache.cacheDir(dir, "testTool", "1.0.1")
        toolcache.cacheDir(dir, "testTool", "1.0.2")

        val found = toolcache.find("testTool", "1.x.x")
        assertNotNull(found)

        println(toolcache.findAllVersions("testTool"))
    }

    @Test
    fun testCacheFile() = GlobalScope.promise {
        if (!isActionRunner) return@promise

        toolcache.downloadTool(licenseUrl, testDir / "test")

        toolcache.cacheFile(testDir / "test", "testFileTool", "1.0.1")

        val found = toolcache.find("testFileTool", "1.x.x")
        assertNotNull(found)
    }
}