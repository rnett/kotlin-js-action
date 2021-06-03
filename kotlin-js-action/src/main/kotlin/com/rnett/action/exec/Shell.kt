package com.rnett.action.exec

private fun String.escapeQuotes() = replace("\"", "\\\"").replace("\'", "\\\'").replace("`", "\\`")

/**
 * Represents a shell used to run commands.
 * @param template The command to use.  '$' will be replaced by the command to run.  See examples in the companion object.  Only quotes and backticks are escaped.
 */
public data class Shell(public val template: String) {
    public companion object{
        public val bash: Shell = Shell("/bin/bash -c \"$\"")
        public val cmd: Shell = Shell("cmd /c \"$\"")
        public val powershell: Shell = Shell("powershell -c \"$\"")
    }

    init {
        val replacements = template.count { it == '$' }
        if(replacements > 1)
            throw IllegalArgumentException("Can't have more than one $ in templace")

        if(replacements < 1)
            throw IllegalArgumentException("Must have one $ in templace")
    }

    public fun withCommand(command: String): String {
        return template.replace("$", command.escapeQuotes())
    }

}