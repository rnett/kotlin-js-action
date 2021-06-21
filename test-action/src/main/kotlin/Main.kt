import com.rnett.action.core.inputs
import com.rnett.action.core.runOrFail
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

suspend fun main(): Unit = runOrFail {
    assertEquals("required-test", inputs["required-input"])
    assertEquals("required-test", inputs.getOptional("required-input"))

    assertFails { inputs["optional-no-default"] }
    assertNull(inputs.getOptional("optional-no-default"))

    assertEquals("Test", inputs["with-default"])
    assertEquals("Test", inputs.getOptional("with-default"))
}