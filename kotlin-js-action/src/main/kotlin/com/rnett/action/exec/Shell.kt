package com.rnett.action.exec

/**
 * Represents a shell used to run commands.
 */
public abstract class Shell(public val escapeWindows: Boolean = true) {
    public abstract fun shellCommand(command: String): String
    public abstract fun args(command: String): Array<String>

    public object bash : ConstantShell("bash") {
        override fun args(command: String): Array<String> = arrayOf("-c", command)
    }

    public object cmd : ConstantShell("cmd") {
        override fun args(command: String): Array<String> = arrayOf("/c", command)
    }

    /**
     * **Note that output redirects with > will be written in utf16-le with a BOM**
     */
    public object powershell : ConstantShell("powershell") {
        override fun args(command: String): Array<String> = arrayOf("-c", command)
    }

}

/**
 * A shell where the shell command is constant.
 */
public abstract class ConstantShell(public val shellCommand: String, escapeWindows: Boolean = true) :
    Shell(escapeWindows) {
    override fun shellCommand(command: String): String = shellCommand
}