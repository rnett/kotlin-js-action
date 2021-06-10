package com.rnett.action

import kotlin.test.Test
import kotlin.test.assertEquals

class TestOS {
    @Test
    fun current() {
        assertEquals(TestEnv.os.lowercase(), currentOS.name.lowercase())
    }
}