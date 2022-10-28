package com.rnett.action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import node.events.Event
import node.stream.Transform
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestStreamUtils {

    @Test
    fun testStreamToFlow() = runTest {
        val stream = Transform(JsObject {
            this.writableObjectMode = true
            this.readableObjectMode = true

            transform = { chunk, encoding, callback ->
                callback(null, chunk)
            }
        })

        stream.write(listOf(1, 2, 3))
        val flow = stream.toFlow<List<Int>>()

        stream.write(listOf(3, 4, 5))
        stream.end()

        val lists = flow.toList()

        assertEquals(
            listOf(
                listOf(1, 2, 3),
                listOf(3, 4, 5)
            ), lists
        )
    }

    @Test
    fun testObjectStream() = runTest {
        val flow = flowOf(listOf(1, 2, 3), listOf(3, 4, 5))
        val stream = flow.toObjectStream(this)
        var i = 0
        stream.on(Event.DATA) { it ->
            if (i == 0)
                assertEquals(listOf(1, 2, 3), it)
            else
                assertEquals(listOf(3, 4, 5), it)
            i++
        }
        stream.resume()
    }

    @Test
    fun canCreateBuffersFromEncodedData() {
        assertEquals(
            "test",
            "test".encodeBase64().decodeBase64()
        )
    }
}