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
        testDir = globalTestDir / "test-$name"

        if (testDir.exists) {
            fs.rmdirSync(testDir.path, JsObject {
                this.recursive = true
            })
        }

        testDir.mkdir()
        testDir.initDir()
        Path.cd(testDir)
    }

    open fun Path.initDir() {

    }

    @AfterTest
    internal fun after() {
        fs.rmdirSync(testDir.path, JsObject {
            this.recursive = true
        })
    }
}