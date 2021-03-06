package com.rnett.action

import com.rnett.action.glob.glob
import com.rnett.action.glob.globFlow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(DelicateCoroutinesApi::class)
class TestGlob : TestWithDir() {
    override fun Path.initDir() {
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
        assertEquals(glob, globFlow, "Glob and Glob flow did not match")
        return glob
    }

    @Test
    fun getFiles() = GlobalScope.promise {
        val files = tryGlob("./**/file*")
        assertEquals(9, files.size)
    }

    @Test
    fun byDir() = GlobalScope.promise {
        val files = tryGlob("./**/innerDir*")
        assertEquals(13, files.size)
    }

    @Test
    fun byDirNoDirs() = GlobalScope.promise {
        val files = tryGlob("./**/innerDir*", matchDirectories = false)
        assertEquals(9, files.size)
    }

    @Test
    fun getDirs() = GlobalScope.promise {
        val files = tryGlob("./**/innerDir*", implicitDescendants = false)
        assertEquals(4, files.size)
    }

}