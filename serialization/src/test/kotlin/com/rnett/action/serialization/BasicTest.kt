package com.rnett.action.serialization

import com.rnett.action.core.env
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.properties.ReadOnlyProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Serializable
data class TestData(val i: Int, val s: String)

private val original = TestData(2, "test")

@OptIn(ExperimentalSerializationApi::class)
internal class BasicTest {

    @Test
    fun testBasics() {
        val json = Json { }
        val delegate = ReadOnlyProperty<Any?, String> { _, _ -> json.encodeToString(original) }
        val typedDelegate: TestData by delegate.deserialize(json)

        assertEquals(original, typedDelegate)
    }

    @Test
    fun testEnv() {
        val json = Json { }
        var envData: TestData? by env.serialized(json)
        envData = null
        assertNull(envData)
        envData = original
        assertEquals(original, envData)
    }
}