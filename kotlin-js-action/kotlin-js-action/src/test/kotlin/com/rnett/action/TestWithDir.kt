package com.rnett.action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.milliseconds

val globalTestDir by lazy { Path(TestEnv.tempDir.trimEnd('/') + "/testdir") }

@OptIn(ExperimentalCoroutinesApi::class)
abstract class TestWithDir {

    private var _testDir: Path? = null

    val testDir: Path
        get() = _testDir ?: error("Test dir not set yet")

    fun runTest(block: suspend TestScope.() -> Unit): TestResult {
        return kotlinx.coroutines.test.runTest {
            doInit()
            block()
        }
    }

    @BeforeTest
    fun before() = kotlinx.coroutines.test.runTest {
        doInit()
    }

    private suspend fun doInit() {
        if (_testDir != null) {
            return
        }

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
        if (_testDir == null)
            _testDir = dir
    }

    open suspend fun Path.initDir() {

    }

    @AfterTest
    internal fun after() = kotlinx.coroutines.test.runTest {
        backgroundScope.launch {
            delay(500.milliseconds)
            node.fs.rmdirSync(testDir.resolve().path, JsObject {
                this.recursive = true
            })
        }
        advanceUntilIdle()
    }
}