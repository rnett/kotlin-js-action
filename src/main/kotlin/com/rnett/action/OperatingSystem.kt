package com.rnett.action

public enum class OperatingSystem {
    Windows, Mac, Linux;
}

public val lineSeperator: String get() = os.EOL

public val currentOS: OperatingSystem
    get() = when (os.platform()) {
        "win32" -> OperatingSystem.Windows
        "darwin" -> OperatingSystem.Mac
        else -> OperatingSystem.Linux
    }
