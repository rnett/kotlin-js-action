package com.rnett.action

import node.path.path
import node.process.Platform

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
            when (node.os.platform()) {
                Platform.win32 -> Windows
                Platform.darwin -> Mac
                else -> Linux
            }
        }

        /**
         * Whether the current OS is Windows
         */
        public inline val isWindows: Boolean get() = current == Windows

        /**
         * Whether the current OS is Max
         */
        public inline val isMac: Boolean get() = current == Mac

        /**
         * Whether the current OS is Linux
         */
        public inline val isLinux: Boolean get() = current == Linux

        /**
         * Whether the current OS is POSIX compliant, i.e. Linux or Mac
         */
        public inline val isPosix: Boolean get() = !isWindows

        /**
         * The line separator of the current operating system
         */
        public inline val lineSeparator: String
            get() = when (current) {
                Windows -> "\r\n"
                Mac -> "\r"
                Linux -> "\n"
            }

        /**
         * Get the current OS's path seperator
         */
        public inline val pathSeperator: String get() = path.sep

        /**
         * Get [os.arch].
         */
        public inline val arch: String get() = node.os.arch()

        /**
         * Get [os.platform]
         */
        public inline val platform: String get() = node.os.platform().name
    }
}
