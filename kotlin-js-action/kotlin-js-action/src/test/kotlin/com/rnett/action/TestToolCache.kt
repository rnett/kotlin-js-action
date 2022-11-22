package com.rnett.action

import com.rnett.action.core.isActionRunner
import com.rnett.action.toolcache.toolcache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TestToolCache : TestWithDir() {

    val licenseUrl = "https://raw.githubusercontent.com/rnett/kotlin-js-action/main/LICENSE"
    suspend fun localLicense() = (Path(TestEnv.projectDirPath) / "LICENSE").readText().replace("\r\n", "\n")

    @Test
    fun testDownload() = runTest {
        val resultFile = testDir / "testLicense"
        val result = toolcache.downloadTool(licenseUrl, resultFile)
        assertEquals(result, resultFile)
        assertEquals(localLicense(), resultFile.readText())

        if (isActionRunner) {
            val tempResult = toolcache.downloadTool(licenseUrl)
            assertEquals(localLicense(), tempResult.readText().replace("\r\n", "\n"))
        }
    }

    @Test
    fun testExtract() = runTest {
        val archivesDir = Path(TestEnv.projectDirPath) / "kotlin-js-action/kotlin-js-action/src/test/resources/archives"
        val extractedDir = testDir / "testExtract"
        archivesDir.children().forEach {

            if (it.name.endsWith(".7z") && !OperatingSystem.isWindows)
                return@forEach

            val extracted = toolcache.extract(it, extractedDir)
            assertEquals("Test file", (extracted / "test/file.txt").readText())
            extractedDir.delete(true)
        }
    }

    @Test
    fun testCacheDir() = runTest {
        if (!isActionRunner) return@runTest

        val dir = testDir / "cacheDir"
        toolcache.downloadTool(licenseUrl, dir / "test")

        toolcache.cacheDir(dir, "testTool", "1.0.1")
        toolcache.cacheDir(dir, "testTool", "1.0.2")

        val found = toolcache.find("testTool", "1.x.x")
        assertNotNull(found)

        assertEquals(listOf("1.0.1", "1.0.2"), toolcache.findAllVersions("testTool").sorted())
    }

    @Test
    fun testCacheFile() = runTest {
        if (!isActionRunner) return@runTest

        toolcache.downloadTool(licenseUrl, testDir / "test")

        toolcache.cacheFile(testDir / "test", "testFileTool", "1.0.1")

        val found = toolcache.find("testFileTool", "1.x.x")
        assertNotNull(found)
    }
}