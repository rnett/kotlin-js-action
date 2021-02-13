package com.rnett.action

/**
 * Operating system Enum.  Limited to GitHub action runners (Windows, Mac, and Linux).
 */
public enum class OperatingSystem {
    Windows, Mac, Linux;
}

/**
 * The line separator of the current operating system
 */
public val lineSeparator: String get() = os.EOL

/**
 * The current operating system.
 */
public val currentOS: OperatingSystem
    get() = when (os.platform()) {
        "win32" -> OperatingSystem.Windows
        "darwin" -> OperatingSystem.Mac
        else -> OperatingSystem.Linux
    }
