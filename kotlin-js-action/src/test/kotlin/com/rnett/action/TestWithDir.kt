package com.rnett.action

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

val globalTestDir by lazy { Path(TestEnv.testCwd.trimEnd('/') + "/testdir") }

@OptIn(DelicateCoroutinesApi::class)
abstract class TestWithDir {
    lateinit var testDir: Path
        private set

    @BeforeTest
    internal fun before() {
        val name = Random.nextLong().toString(20) + Random.nextLong().toString(20)
        val dir = globalTestDir / "test-$name"

        if (dir.exists) {
            fs.rmdirSync(dir.path, JsObject {
                this.recursive = true
            })
        }

        dir.mkdir()
        dir.initDir()
        Path.cd(dir)
        testDir = dir
    }

    open fun Path.initDir() {

    }

    @AfterTest
    internal fun after() {
        //TODO do cleanup.  I'm returning promises, so sometimes they would be executed after the cleanup
//        Path.cd(globalTestDir)
//        fs.rmdirSync(testDir.path, JsObject {
//            this.recursive = true
//        })
    }
}