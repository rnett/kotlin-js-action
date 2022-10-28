package com.rnett.action

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class TestPath : TestWithDir() {

    @Test
    fun testCwd() {
        assertEquals(currentProcess.cwd(), Path.cwd.path)
        assertEquals(currentProcess.cwd(), Path(".").path, "Expansion")
    }

    @Test
    fun testUserHome() {
        assertEquals(TestEnv.userHome, Path.userHome.path)
        assertEquals(TestEnv.userHome, Path("~").path, "Expansion")
    }

    @Test
    fun testExists() {
        assertTrue((TestEnv.projectDir / ".gitignore").exists)
        assertTrue((TestEnv.projectDir / "./.gitignore").exists)

        assertFalse((TestEnv.projectDir / "nonexistant").exists)
        assertFalse((TestEnv.projectDir / "./nonexistant").exists)
    }

    @Test
    fun testStat() = runTest {
        assertNull(Path("nonexistant").stats())

        val file = TestEnv.projectDir / "LICENSE"
        val fileStats = file.stats()
        assertNotNull(fileStats)

        assertTrue(fileStats.isFile())
        assertTrue(file.isFile())

        assertFalse(fileStats.isDirectory())
        assertFalse(file.isDir())

        assertEquals(11, (fileStats.size.toDouble() / 1000).toInt())

        val dir = TestEnv.projectDir / "kotlin-js-action/kotlin-js-action"
        val dirStats = dir.stats()
        assertNotNull(dirStats)

        assertFalse(dirStats.isFile())
        assertFalse(dir.isFile())

        assertTrue(dirStats.isDirectory())
        assertTrue(dir.isDir())
    }

    @Test
    fun testManipulation() = runTest {
        val dir = TestEnv.projectDir / "./kotlin-js-action/kotlin-js-action/src"
        val child = dir / "main"
        val parent = dir.parent

        assertTrue(dir.isDir())
        assertTrue(parent.isDir())
        assertTrue(child.isDir())

        assertTrue(child.isDescendantOf(parent))
        assertTrue(child.isDescendantOf(dir))
        assertTrue(dir.isDescendantOf(parent))

        assertEquals(TestEnv.projectDir, dir.ancestor(3))
    }

    @Test
    fun testWalking() = runTest {
        val dir = (TestEnv.projectDir / "./kotlin-js-action/kotlin-js-action/src")
        assertFalse(dir.isDirEmpty())
        assertEquals(listOf(dir / "main", dir / "test"), dir.children())
    }

    @Test
    fun testMkdir() = runTest {
        val new = testDir / "newDir1"
        new.mkdir()
        new.mkdir() // for existsOk
        assertTrue(new.exists)

        val newNested = testDir / "newDir2" / "innerNew"
        newNested.mkdir()
        newNested.mkdir() // for existsOk
        assertTrue(newNested.exists)

        assertFails { new.mkdir(existsOk = false) }
        assertFails { newNested.mkdir(existsOk = false) }

        val newNested2 = testDir / "newDir3" / "innerNew"
        assertFails { newNested2.mkdir(parents = false) }
    }

    @Test
    fun testTouch() = runTest {
        val new = testDir / "testTouch"
        assertFalse(new.exists)
        new.touch()
        assertTrue(new.isFile())
    }

    @Test
    fun testDelete() = runTest {
        val dir = testDir / "delete"
        dir.mkdir()
        (dir / "file").touch()
        assertFalse(dir.isDirEmpty())

        assertFails { dir.delete(false) }

        dir.delete(true)
        assertFalse(dir.exists)

        val file = (testDir / "testDelete").touch()
        assertTrue(file.exists)
        file.delete()
        assertFalse(file.exists)
    }

    @Test
    fun testCopyInto() = runTest {
        val destDir = testDir / "copyDest"
        destDir.mkdir()

        val sourceDir = (testDir / "copyDir").mkdir().apply {
            (this / "file").write("test")
        }

        sourceDir.copyInto(destDir)

        //FIXME doesn't work, not my doing
//        assertFails { sourceDir.copyInto(destDir, force = false) }
        assertEquals("test", (destDir / "copyDir/file").readText())

        val sourceFile = (testDir / "copyFile").apply { write("test2") }
        sourceFile.copyInto(destDir)
        assertEquals("test2", (destDir / "copyFile").readText())

        val newDestDir = testDir / "copyDest"
        sourceDir.copyInto(newDestDir)
        assertEquals("test", (newDestDir / "copyDir/file").readText())
    }

    @Test
    fun testCopy() = runTest {
        val destDir = testDir / "copyDest3"
        val source = testDir / "copySource3"
        source.mkdir()
        (source / "file").write("test")

        source.copy(destDir)
        assertEquals("test", (destDir / "file").readText())
        //FIXME doesn't work, not my doing
//        assertFails { source.copy(destDir, force = false) }
    }

    @Test
    fun testCopyChildren() = runTest {
        val dest = testDir / "copyDest2"
        val sourceDir = (testDir / "copy3").mkdir().apply {
            (this / "file2").write("test3")
        }
        sourceDir.copyChildrenInto(dest)

        assertEquals("test3", (dest / "file2").readText())
    }

    @Test
    fun testMoveInto() = runTest {
        val destDir = testDir / "moveDest"
        destDir.mkdir()

        val sourceDir = (testDir / "moveDir").mkdir().apply {
            (this / "file").write("test")
        }
        val sourceDir2 = testDir / "moveDir2"
        sourceDir.copyInto(sourceDir2)

        sourceDir.moveInto(destDir)
        assertFails { sourceDir.moveInto(destDir, force = false) }
        assertEquals("test", (destDir / "moveDir/file").readText())
        assertFalse(sourceDir.exists)

        val sourceFile = (testDir / "moveFile").apply { write("test2") }
        sourceFile.moveInto(destDir)
        assertEquals("test2", (destDir / "moveFile").readText())
        assertFalse(sourceFile.exists)

        val newDestDir = testDir / "moveDest"
        sourceDir2.moveInto(newDestDir)
        assertEquals("test", (newDestDir / "moveDir/file").readText())
        assertFalse(sourceDir2.exists)
    }

    @Test
    fun testMove() = runTest {
        val destDir = testDir / "moveDest3"
        val source = testDir / "moveSource3"
        source.mkdir()
        (source / "file").write("test")

        val source2 = (testDir / "moveSource32").apply { source.copy(this) }

        source.move(destDir)
        source2.move(destDir)

        assertEquals("test", (destDir / "file").readText())
        assertFalse(source.exists)
        assertFails { source.move(destDir, force = false) }
    }

    @Test
    fun testRename() = runTest {
        val destDir = testDir / "newRename"
        val source = testDir / "reanemSource"
        source.mkdir()
        (source / "file").write("test")

        source.rename(destDir.name)
        assertEquals("test", (destDir / "file").readText())
        assertFalse(source.exists)

        (testDir / "takenName").touch()

        assertFails { source.rename("takenName") }
    }

    @Test
    fun testMoveChildren() = runTest {
        val dest = testDir / "moveDest2"
        val sourceDir = (testDir / "move2").mkdir().apply {
            (this / "file2").write("test3")
        }
        sourceDir.moveChildrenInto(dest)
        assertEquals("test3", (dest / "file2").readText())
        assertFalse((sourceDir / "file2").exists)
    }

    @Test
    fun testPresetRead() = runTest {
        assertEquals(
            "# Project exclude paths",
            Path(TestEnv.projectDirPath).descendant(".gitignore").readText().lines().first()
        )
    }

    @Test
    fun testRead() = runTest {
        val file = testDir / "readFile"
        file.write("testData")
        assertEquals("testData", file.readText())
        assertEquals("testData", file.readBytes().decodeToString())
    }

    @Test
    fun testWrite() = runTest {
        val textFile = testDir / "writeTextFile"
        textFile.write("testData")
        assertEquals("testData", textFile.readText())

        val bytesFile = testDir / "dir/writeBytesFile"
        bytesFile.write("testData".encodeToByteArray())
        assertEquals("testData", bytesFile.readBytes().decodeToString())

        val bufferFile = testDir / "dir/writeBufferFile"
        bufferFile.write("test".encodeToByteArray().asBuffer())
        assertEquals("test", bufferFile.readText())
    }

    @Test
    fun testAppend() = runTest {
        val textFile = testDir / "writeTextFile"
        textFile.write("test")
        textFile.append("Data")
        assertEquals("testData", textFile.readText())

        val bytesFile = testDir / "dir/writeBytesFile"
        bytesFile.write("test".encodeToByteArray())
        bytesFile.append("Data".encodeToByteArray())
        assertEquals("testData", bytesFile.readBytes().decodeToString())
    }

    //TODO stream tests

}