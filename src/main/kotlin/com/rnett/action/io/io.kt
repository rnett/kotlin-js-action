package com.rnett.action.io

import com.rnett.action.JsObject
import com.rnett.action.Path
import kotlinx.coroutines.await

public object io {
    public suspend fun cp(source: String, dest: String, recursive: Boolean = false, force: Boolean = true) {
        internal.io.cp(source, dest, JsObject {
            this.recursive = recursive
            this.force = force
        }).await()
    }

    public suspend fun mv(source: String, dest: String, force: Boolean = true) {
        internal.io.mv(source, dest, JsObject {
            this.force = force
        }).await()
    }

    public suspend fun rmRF(inputPath: String) {
        internal.io.rmRF(inputPath).await()
    }

    public suspend fun mkdirP(path: String) {
        internal.io.mkdirP(path).await()
    }

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

    public suspend fun cp(source: Path, dest: Path, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source.path, dest.path, recursive, force)

    public suspend fun mv(source: Path, dest: Path, force: Boolean = true): Unit = mv(source.path, dest.path, force)

    public suspend fun cp(source: Path, dest: String, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source.path, dest, recursive, force)

    public suspend fun mv(source: Path, dest: String, force: Boolean = true): Unit = mv(source.path, dest, force)

    public suspend fun cp(source: String, dest: Path, recursive: Boolean = false, force: Boolean = true): Unit =
        cp(source, dest.path, recursive, force)

    public suspend fun mv(source: String, dest: Path, force: Boolean = true): Unit = mv(source, dest.path, force)

    public suspend fun rmRF(inputPath: Path): Unit = rmRF(inputPath.path)

    public suspend fun mkdirP(path: Path): Unit = mkdirP(path.path)

    public suspend fun which(tool: Path): Path? = which(tool.path)?.let { Path(it) }
}