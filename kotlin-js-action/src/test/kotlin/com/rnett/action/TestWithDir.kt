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

    private val random = Random

    @BeforeTest
    internal fun before() {
        val name = Random.nextLong().toString(16)
        testDir = globalTestDir / "test-$name"

        if (testDir.exists) {
            fs.rmdirSync(testDir.path, JsObject {
                this.recursive = true
            })
        }

        testDir.mkdir()
        Path.cd(testDir)
        testDir.initDir()
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