package com.rnett.action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

val globalTestDir by lazy { Path(TestEnv.testCwd.trimEnd('/') + "/testdir") }

@OptIn(ExperimentalCoroutinesApi::class)
abstract class TestWithDir {
    lateinit var testDir: Path
        private set

    @BeforeTest
    internal fun before() = runTest {
        val name = Random.nextLong().toString(20) + Random.nextLong().toString(20)
        val dir = globalTestDir / "test-$name"

        if (dir.exists) {
            node.fs.rmdirSync(dir.path, JsObject {
                this.recursive = true
            })
        }

        dir.mkdir()
        dir.initDir()
        Path.cd(dir)
        testDir = dir
    }

    open suspend fun Path.initDir() {

    }

    @AfterTest
    internal fun after() {
        //TODO do cleanup.  I'm returning promises, so sometimes they would be executed after the cleanup.
        // Prob needs coroutines-test
//        Path.cd(globalTestDir)
//        fs.rmdirSync(testDir.path, JsObject {
//            this.recursive = true
//        })
    }
}