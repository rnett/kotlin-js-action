package com.rnett.action.io

import com.rnett.action.JsObject
import com.rnett.action.Path
import kotlinx.coroutines.await

/**
 * Wrappers for [`@actions/io`](https://github.com/actions/toolkit/tree/main/packages/io).
 */
public object io {

    /**
     * Copy files.
     */
    public suspend fun cp(source: String, dest: String, recursive: Boolean = false, force: Boolean = true) {
        internal.io.cp(source, dest, JsObject {
            this.recursive = recursive
            this.force = force
        }).await()
    }

    /**
     * Move files.
     */
    public suspend fun mv(source: String, dest: String, force: Boolean = true) {
        internal.io.mv(source, dest, JsObject {
            this.force = force
        }).await()
    }

    /**
     * Remove files recursively (`rm -rf`).
     */
    public suspend fun rmRF(inputPath: String) {
        internal.io.rmRF(inputPath).await()
    }

    /**
     * Create a directory and any parents that don't exist.
     */
    public suspend fun mkdirP(path: String) {
        internal.io.mkdirP(path).await()
    }

    /**
     * Get the location of a tool, or `null` if it can't be found.
     */
    public suspend fun which(tool: String): String? {
        return try {
            internal.io.which(tool, check = true).await()
        } catch (error: Throwable) {
            if (error.message != null && "Unable to locate executable file" in error.message!!)
                null
            else
                throw error
        }
    }
    /**
     * Copy files.
     */

    public suspend fun cp(source: Path, dest: Path, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source.path, dest.path, recursive, force)

    /**
     * Move files.
     */
    public suspend fun mv(source: Path, dest: Path, force: Boolean = true): Unit = mv(source.path, dest.path, force)

    /**
     * Copy files.
     */
    public suspend fun cp(source: Path, dest: String, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source.path, dest, recursive, force)

    /**
     * Move files.
     */
    public suspend fun mv(source: Path, dest: String, force: Boolean = true): Unit = mv(source.path, dest, force)

    /**
     * Copy files.
     */
    public suspend fun cp(source: String, dest: Path, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source, dest.path, recursive, force)

    /**
     * Move files.
     */
    public suspend fun mv(source: String, dest: Path, force: Boolean = true): Unit = mv(source, dest.path, force)

    /**
     * Remove files recursively (`rm -rf`).
     */
    public suspend fun rmRF(inputPath: Path): Unit = rmRF(inputPath.path)

    /**
     * Create a directory and any parents that don't exist.
     */
    public suspend fun mkdirP(path: Path): Unit = mkdirP(path.path)

    /**
     * Get the location of a tool, or `null` if it can't be found.
     */
    public suspend fun which(tool: Path): Path? = which(tool.path)?.let { Path(it) }
}