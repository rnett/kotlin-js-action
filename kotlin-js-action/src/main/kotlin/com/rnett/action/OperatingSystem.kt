package com.rnett.action

import path.path

/**
 * Operating system Enum.  Limited to GitHub action runners (Windows, Mac, and Linux).
 */
public enum class OperatingSystem {
    Windows, Mac, Linux;

    public companion object {

        /**
         * The current operating system.
         */
        public val current: OperatingSystem by lazy {
            when (os.platform()) {
                "win32" -> Windows
                "darwin" -> Mac
                else -> Linux
            }
        }

        /**
         * Whether the current OS is Windows
         */
        public val isWindows: Boolean get() = current == Windows

        /**
         * Whether the current OS is Max
         */
        public val isMac: Boolean get() = current == Mac

        /**
         * Whether the current OS is Linux
         */
        public val isLinux: Boolean get() = current == Linux

        /**
         * Whether the current OS is POSIX compliant, i.e. Linux or Mac
         */
        public val isPosix: Boolean get() = !isWindows

        /**
         * The line separator of the current operating system
         */
        public val lineSeparator: String get() = os.EOL

        /**
         * Get the current OS's path seperator
         */
        public val pathSeperator: String get() = path.sep
    }
}
