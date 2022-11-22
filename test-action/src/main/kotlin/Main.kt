import com.rnett.action.core.SummaryTableCell
import com.rnett.action.core.env
import com.rnett.action.core.inputs
import com.rnett.action.core.runAction
import com.rnett.action.core.summary
import com.rnett.action.delegates.lines
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

const val requiredInputKey = "required-input"
const val requiredInputValue = "required-test"

const val optionalNoDefaultKey = "optional-no-default"

const val withDefaultKey = "with-default"
const val withDefaultValue = "Test"

const val multilineKey = "multiline"
val multilineValue = listOf("test1", "test2")

const val testEnvKey = "test-env"
const val testEnvValue = "test2"

const val noEnvKey = "no-env"

suspend fun main() = runAction {
    assertEquals(requiredInputValue, inputs[requiredInputKey])
    assertEquals(requiredInputValue, inputs.getRequired(requiredInputKey))
    assertEquals(requiredInputValue, inputs.getOptional(requiredInputKey))
    val requiredInput by inputs
    assertEquals(requiredInputValue, requiredInput)

    assertFails { inputs.getRequired(optionalNoDefaultKey) }
    assertNull(inputs.getOptional(optionalNoDefaultKey))
    val optionalNoDefault by inputs.optional
    assertNull(optionalNoDefault)

    assertEquals(withDefaultValue, inputs[withDefaultKey])
    assertEquals(withDefaultValue, inputs.getRequired(withDefaultKey))
    assertEquals(withDefaultValue, inputs.getOptional(withDefaultKey))
    val withDefault by inputs
    assertEquals(withDefaultValue, withDefault)

    assertEquals(multilineValue, inputs[multilineKey]?.split("\n"))
    assertEquals(multilineValue, inputs.getRequired(multilineKey).split("\n"))
    assertEquals(multilineValue, inputs.getOptional(multilineKey)?.split("\n"))
    val multiline by inputs.lines()
    assertEquals(multilineValue, multiline)

    assertEquals(testEnvValue, env[testEnvKey])
    assertEquals(testEnvValue, env.getRequired(testEnvKey))
    val testEnv by env(testEnvKey)
    assertEquals(testEnvValue, testEnv)

    assertNull(env[noEnvKey])
    assertFails { env.getRequired(noEnvKey) }
    var noEnv by env(noEnvKey)
    assertNull(noEnv)
    noEnv = "test"
    assertEquals("test", noEnv)
    assertEquals("test", env.getRequired(noEnvKey))

    summary.append {
        h2("Test")
        hr()
        addTable(
            listOf(SummaryTableCell("Test", true), SummaryTableCell("Test2", true)),
            listOf(SummaryTableCell("1"), SummaryTableCell("2"))
        )
    }

    println("Tests done!")
}