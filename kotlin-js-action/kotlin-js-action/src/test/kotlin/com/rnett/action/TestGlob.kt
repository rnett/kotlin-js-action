package com.rnett.action

import com.rnett.action.glob.glob
import com.rnett.action.glob.globFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestGlob : TestWithDir() {
    override suspend fun Path.initDir() {
        descendant("testDir1/innerDir1/file1").touch()
        descendant("testDir1/innerDir1/file2").touch()
        descendant("testDir1/innerDir1/file3").touch()
        descendant("testDir1/innerDir2/file1").touch()
        descendant("testDir1/innerDir2/file2").touch()
        descendant("testDir2/innerDir1/file1").touch()
        descendant("testDir2/innerDir1/file2").touch()
        descendant("testDir2/innerDir1/file3").touch()
        descendant("testDir2/innerDir2/file1").touch()
    }

    private suspend fun tryGlob(
        vararg patterns: String,
        followSymbolicLinks: Boolean = true,
        implicitDescendants: Boolean = true,
        omitBrokenSymbolicLinks: Boolean = true,
        matchDirectories: Boolean = true
    ): List<Path> {
        val glob = glob(
            *patterns,
            followSymbolicLinks = followSymbolicLinks,
            implicitDescendants = implicitDescendants,
            omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
            matchDirectories = matchDirectories
        )
        val globFlow = globFlow(
            *patterns,
            followSymbolicLinks = followSymbolicLinks,
            implicitDescendants = implicitDescendants,
            omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
            matchDirectories = matchDirectories
        ).toList()
        assertEquals(glob.toSet(), globFlow.toSet(), "Glob and Glob flow did not match")
        return glob
    }

    @Test
    fun getFiles() = runTest {
        val files = tryGlob("./**/file*")
        assertEquals(9, files.size)
    }

    @Test
    fun byDir() = runTest {
        val files = tryGlob("./**/innerDir*")
        assertEquals(13, files.size)
    }

    @Test
    fun byDirNoDirs() = runTest {
        val files = tryGlob("./**/innerDir*", matchDirectories = false)
        assertEquals(9, files.size)
    }

    @Test
    fun getDirs() = runTest {
        val files = tryGlob("./**/innerDir*", implicitDescendants = false)
        assertEquals(4, files.size)
    }

}