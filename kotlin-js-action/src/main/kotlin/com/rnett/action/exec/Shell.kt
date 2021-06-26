package com.rnett.action.exec

private fun String.escapePowershell() =
    replace("`", "``")
        .replace("\"", "`\"")
        .replace("\'", "`\'")
        .replace("#", "`#")
        .replace("\n", "`n")
        .replace("\r", "`r")
        .replace("\t", "`t")

private fun String.escapeCmd() =
    replace("^", "^^")
        .replace("\"", "^\"")
        .replace("\'", "^\'")
        .replace("`", "^`")

/**
 * Represents a shell used to run commands.
 */
public abstract class Shell(public val shellCommand: String, public val escapeWindows: Boolean = true) {
    public abstract fun args(command: String): Array<String>

    public object bash : Shell("bash") {
        override fun args(command: String): Array<String> = arrayOf(
            "-c",
            "$\'${command.replace("\'", "\\\'")}\'"
        )
    }

    public object cmd : Shell("cmd") {
        override fun args(command: String): Array<String> = arrayOf("/c", command)
    }

    /**
     * **Note that output redirects with > will be written in utf16-le with a BOM**
     */
    public object powershell : Shell("powershell") {
        override fun args(command: String): Array<String> = arrayOf("-c", command)
    }

}