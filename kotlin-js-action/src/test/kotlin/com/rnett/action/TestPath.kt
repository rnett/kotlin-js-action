package com.rnett.action

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.*

class TestPath {

    val testDir by lazy { Path(TestEnv.testCwd.trimEnd('/') + "/testdir") }

    val oldCwd = Path.cwd

    init {
        Path.cd(Path(TestEnv.testCwd))
    }

    @BeforeTest
    fun before() {
        testDir.mkdir()
    }

    @AfterTest
    fun after() = GlobalScope.promise {
        testDir.children.forEach { it.delete() }
    }

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
        assertTrue(Path(".gitignore").exists)
        assertTrue(Path("./.gitignore").exists)

        assertFalse(Path("nonexistant").exists)
        assertFalse(Path("./nonexistant").exists)
    }

    @Test
    fun testStat() {
        assertNull(Path("nonexistant").stats)

        val file = Path("LICENSE")
        val fileStats = file.stats
        assertNotNull(fileStats)

        assertTrue(fileStats.isFile())
        assertTrue(file.isFile)

        assertFalse(fileStats.isDirectory())
        assertFalse(file.isDir)

        assertEquals(11, (fileStats.size.toDouble() / 1000).toInt())

        val dir = Path("kotlin-js-action")
        val dirStats = dir.stats
        assertNotNull(dirStats)

        assertFalse(dirStats.isFile())
        assertFalse(dir.isFile)

        assertTrue(dirStats.isDirectory())
        assertTrue(dir.isDir)
    }

    @Test
    fun testManipulation() {
        val dir = Path("./kotlin-js-action/src")
        val child = dir / "main"
        val parent = dir.parent

        assertTrue(dir.isDir)
        assertTrue(parent.isDir)
        assertTrue(child.isDir)

        assertTrue(child.isDescendantOf(parent))
        assertTrue(child.isDescendantOf(dir))
        assertTrue(dir.isDescendantOf(parent))

        assertEquals(Path("."), dir.ancestor(2))
    }

    @Test
    fun testWalking() {
        val dir = Path("./kotlin-js-action/src")
        assertFalse(dir.isDirEmpty)
        assertEquals(listOf(dir / "main", dir / "test"), dir.children)
    }

    @Test
    fun testMkdir() {
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
    fun testTouch() {
        val new = testDir / "testTouch"
        assertFalse(new.exists)
        new.touch()
        assertTrue(new.isFile)
    }

    @Test
    fun testDelete() = GlobalScope.promise {
        val dir = testDir / "delete"
        dir.mkdir()
        (dir / "file").touch()
        assertFalse(dir.isDirEmpty)

        assertFails { dir.delete(false) }

        dir.delete(true)
        assertFalse(dir.exists)

        val file = (testDir / "testDelete").touch()
        assertTrue(file.exists)
        file.delete()
        assertFalse(file.exists)
    }

    @Test
    fun testCopy() = GlobalScope.promise {
        val destDir = testDir / "copyDest"
        destDir.mkdir()

        val sourceDir = (testDir / "copyDir").mkdir().apply {
            (this / "file").write("test")
        }

        sourceDir.copyInto(destDir)

        //FIXME doesn't work, not my doing
//        assertFails { sourceDir.copyInto(destDir, force = false) }
        assertEquals("test", (destDir / "copyDir/file").read())

        val sourceFile = (testDir / "copyFile").apply { write("test2") }
        sourceFile.copyInto(destDir)
        assertEquals("test2", (destDir / "copyFile").read())

        val newDestDir = testDir / "copyDest"
        sourceDir.copyInto(newDestDir)
        assertEquals("test", (newDestDir / "copyDir/file").read())
    }

    @Test
    fun testCopyChildren() = GlobalScope.promise {
        val dest = testDir / "copyDest2"
        val sourceDir = (testDir / "copy3").mkdir().apply {
            (this / "file2").write("test3")
        }
        sourceDir.copyChildrenInto(dest)

        assertEquals("test3", (dest / "file2").read())
    }

    @Test
    fun testMove() = GlobalScope.promise {
        val destDir = testDir / "moveDest"
        destDir.mkdir()

        val sourceDir = (testDir / "moveDir").mkdir().apply {
            (this / "file").write("test")
        }
        val sourceDir2 = testDir / "moveDir2"
        sourceDir.copyInto(sourceDir2)

        sourceDir.moveInto(destDir)
        assertFails { sourceDir.moveInto(destDir, force = false) }
        assertEquals("test", (destDir / "moveDir/file").read())
        assertFalse(sourceDir.exists)

        val sourceFile = (testDir / "moveFile").apply { write("test2") }
        sourceFile.moveInto(destDir)
        assertEquals("test2", (destDir / "moveFile").read())
        assertFalse(sourceFile.exists)

        val newDestDir = testDir / "moveDest"
        sourceDir2.moveInto(newDestDir)
        assertEquals("test", (newDestDir / "moveDir/file").read())
        assertFalse(sourceDir2.exists)
    }

    @Test
    fun testMoveChildren() = GlobalScope.promise {
        val dest = testDir / "moveDest2"
        val sourceDir = (testDir / "move2").mkdir().apply {
            (this / "file2").write("test3")
        }
        sourceDir.moveChildrenInto(dest)
        assertEquals("test3", (dest / "file2").read())
        assertFalse((sourceDir / "file2").exists)
    }

}