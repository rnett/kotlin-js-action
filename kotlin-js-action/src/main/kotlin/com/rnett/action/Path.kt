package com.rnett.action

import com.rnett.action.io.io
import fs.*
import path.path as platformPath

/**
 * Path handling utilities modeled after Python's `pathlib`.  By default resolves paths on creation.  Disabling this may lead to errors.
 */
public class Path(rawPath: String, resolve: Boolean = true) {

    /**
     * The raw path.
     */
    public val path: String = if (resolve) resolve(rawPath)!! else rawPath!!

    /**
     * Resolve a path.  Done by default on creation.
     */
    public fun resolve(): Path = Path(resolve(path))

    public companion object {
        /**
         * The current working directory.
         */
        public var cwd: Path
            get() = Path(currentProcess.cwd())
            set(value) {
                cd(value)
            }

        /**
         * The current user's home dir.
         */
        public val userHome: Path get() = Path(os.homedir())

        /**
         * The current OS's path separator.
         */
        public val pathSeparator: String get() = OperatingSystem.pathSeperator

        /**
         * Change the working directory
         */
        public fun cd(newWD: Path) {
            currentProcess.chdir(newWD.path)
        }

        /**
         * Resolve a raw path, replacing `~` if present.
         */
        public fun resolve(rawPath: String): String {
            val newRawPath = if (rawPath.startsWith("~")) {
                platformPath.join(os.homedir(), rawPath.removePrefix("~"))
            } else
                rawPath

            return platformPath.resolve(newRawPath)!!
        }
    }

    /**
     * Get a descendant.
     */
    public operator fun div(rest: String): Path = Path(
        platformPath.join(
            path,
            rest
        )
    )

    /**
     * Get a descendant.
     */
    public operator fun div(rest: Path): Path = this / rest.path

    /**
     * Get a descendant.
     */
    public fun descendant(rest: String): Path = this / rest

    /**
     * Get a descendant.
     */
    public fun descendant(rest: Path): Path = this / rest

    /**
     * Get if this is a descendant of [ancestor].
     */
    public fun isDescendantOf(ancestor: Path): Boolean = this.path.startsWith(ancestor.path)

    /**
     * Get if this is a descendant of [ancestor].
     */
    public fun isDescendantOf(ancestor: String): Boolean = isDescendantOf(Path(ancestor))

    /**
     * Get the path segments of this path.
     */
    public val segments: List<String> by lazy { path.split(OperatingSystem.pathSeperator).filter { it.isNotEmpty() } }

    /**
     * See if this path contains a segment.  Will return false if [Path.seperator] is in [segment].
     */
    public operator fun contains(segment: String): Boolean = segment in segments

    /**
     * Get the file/directory name (with extension).
     */
    public val name: String by lazy { platformPath.basename(path) }

    /**
     * Get the file extension.
     */
    public val extension: String by lazy { platformPath.extname(path) }

    /**
     * Get the parent [Path].
     */
    public val parent: Path by lazy { Path(platformPath.dirname(path)) }

    /**
     * Repeatedly gets [parent].  `ancestor(0)` is `this`, `ancestor(1)` is `parent`, and so on.
     */
    public fun ancestor(great: Int): Path {
        var current = this
        repeat(great) {
            current = current.parent
        }
        return current
    }

    /**
     * Gets whether this path is absolute.
     */
    public val isAbsolute: Boolean by lazy { platformPath.isAbsolute(path) }

    /**
     * Gets whether this path exists.
     */
    public val exists: Boolean get() = fs.existsSync(path)

    /**
     * Get [Stats] for this path, if it exists.
     */
    public val stats: Stats? get() = if (exists) fs.lstatSync(path) else null

    /**
     * Get whether this path exists and is a file.
     */
    public val isFile: Boolean get() = stats?.isFile() == true

    /**
     * Get whether this path exists and is a directory.
     */
    public val isDir: Boolean get() = stats?.isDirectory() == true

    /**
     * Throw if this path is not a file, or doesn't exist and [requireExists] is true (as it is by default).
     *
     * @return this
     */
    public fun requireFile(requireExists: Boolean = true): Path {
        val stats = stats
        val exists = stats != null

        if (!requireExists && !exists)
            return this
        if (!exists)
            error("File does not exist: $this")

        if (stats?.isFile() == false)
            error("Path $this is not a file")
        return this
    }

    /**
     * Throw if this path is not a directory, or doesn't exist and [requireExists] is true (as it is by default).
     *
     * @return this
     */
    public fun requireDirectory(requireExists: Boolean = true): Path {
        val stats = stats
        val exists = stats != null

        if (!requireExists && !exists)
            return this
        if (!exists)
            error("Directory does not exist: $this")

        if (stats?.isDirectory() == false)
            error("Path $this is not a directory")
        return this
    }

    /**
     * Get whether this directory is empty.
     *
     * Will throw if this isn't a directory or doesn't exist.
     */
    public val isDirEmpty: Boolean
        get() {
            requireDirectory()
            return fs.readdirSync(path, JsObject<`T$38`> {
                this.withFileTypes = true
            }).isEmpty()
        }

    /**
     * Get whether this directory is empty.
     *
     * Will throw if this isn't a directory.
     */
    public val children: List<Path>
        get() {
            requireDirectory()
            return fs.readdirSync(path, JsObject<`T$38`> {
                this.withFileTypes = true
            }).map { this / it.name }
        }

    /**
     * Make this directory.
     *
     * @param parents whether to also make parents that don't exist
     * @param existsOk if false, will throw if the current path exists
     * @return this
     */
    public fun mkdir(parents: Boolean = true, existsOk: Boolean = true): Path {
        val stats = stats
        if (stats != null) {
            if (!existsOk)
                error("Path $path already exists")

            if (!stats.isDirectory())
                error("Path $path exists, but is not a directory")

            return this
        }

        fs.mkdirSync(path, JsObject<MakeDirectoryOptions> {
            this.recursive = parents
        })

        return this
    }

    /**
     * Create an empty file if it doesn't exist, creating parent directories if necessary.
     * @return this
     */
    public fun touch(): Path {
        val stats = stats
        if (stats != null) {
            if (stats.isFile())
                return this
            else
                error("Path $path exists, but is not a file")
        }

        parent.mkdir()
        write("")
        return this
    }

    /**
     * Delete this path.
     *
     * If [recursive] is false but this is a directory and has children, throws.
     */
    public suspend fun delete(recursive: Boolean = false) {
        if (!recursive && isDir && !isDirEmpty)
            error("Can't delete directory $path, it is not empty")

        io.rmRF(path)
    }

    /**
     * Copy this file or directory **into** [destDir], creating it if it does not exist.
     * Note that this differs from `cp` which will sometimes copy into.
     *
     * @param recursive whether to recursively copy children
     * @param force whether to overwrite files in [destDir].  **A `false` value is sometimes ignored, do not rely on.**
     * @see io.cp
     */
    public suspend fun copyInto(destDir: Path, recursive: Boolean = true, force: Boolean = true) {
        val stats = destDir.stats
        if (stats == null) {
            destDir.mkdir()
        } else if (stats.isFile()) {
            error("Destination $destDir is a file, can't copy into a file.")
        }

        io.cp(path, destDir, recursive, force, true)
    }

    /**
     * Copy this directory's children into the directory [destDir], creating it if it does not exist.
     *
     * @param recursive whether to recursively copy children.
     * Note that this applies to this directories children, not to itself.
     * @param force whether to overwrite files in [destDir].  **A `false` value is sometimes ignored, do not rely on.**
     */
    public suspend fun copyChildrenInto(destDir: Path, recursive: Boolean = true, force: Boolean = true) {
        children.forEach {
            it.copyInto(destDir, recursive, force)
        }
    }

    /**
     * Copies this file or directory **to** [dest].
     * Never copies into, for that use [copyInto].
     * Note that this differs from `cp` which will sometimes copy into.
     *
     * @param recursive whether to recursively copy children
     * @param force whether to overwrite files in [dest].  **A `false` value is sometimes ignored, do not rely on.**
     * @see io.cp
     */
    public suspend fun copy(dest: Path, recursive: Boolean = true, force: Boolean = true) {
        io.cp(path, dest, recursive, force, false)
    }

    /**
     * Move this file or directory **into** [destDir], creating it if it does not exist.
     * Note that this differs from `mv` which will sometimes move into.
     *
     * @param force whether to overwrite files in [destDir].
     * @see io.mv
     */
    public suspend fun moveInto(destDir: Path, force: Boolean = true) {
        val stats = destDir.stats
        if (stats == null) {
            destDir.mkdir()
        } else if (stats.isFile()) {
            error("Destination $destDir is a file, can't move into a file.")
        }

        io.mv(path, destDir, force)
    }

    /**
     * Move this directory's children into the directory [destDir], creating it if it does not exist.
     *
     * @param force whether to overwrite files in [destDir].
     */
    public suspend fun moveChildrenInto(destDir: Path, force: Boolean = true) {
        children.forEach { it.moveInto(destDir, force) }
    }


    /**
     * Move this file or directory **to** [dest].
     * Never moves into, for that use [copyInto].
     * Note that this differs from `mv` which will sometimes copy into.
     *
     * @param force whether to overwrite files in [dest].
     * @see io.cp
     */
    public suspend fun move(dest: Path, force: Boolean = true) {
        if (dest.exists) {
            if (force)
                dest.delete(true)
            else
                error("Destination $dest exists, but force is false.")
        }


        fs.renameSync(path, dest.path)
    }

    public fun rename(newName: String) {
        if (newName == name)
            return

        val newPath = (parent / newName)
        if (newPath.exists)
            error("File already exists with new name: $newPath")
        fs.renameSync(path, newPath.path)
    }

    /**
     * Read this file.
     */
    public fun readText(encoding: String = "utf8"): String {
        requireFile()
        return fs.readFileSync(path, JsObject<`T$43`> {
            this.encoding = encoding
        })
    }

    /**
     * Read this file.
     */
    public fun readBytes(): ByteArray {
        requireFile()
        return fs.readFileSync(path, JsObject<`T$42`> {
            this.encoding = encoding
        }).let { buffer ->
            //FIXME duplicates memory, need a better way of doing this
            ByteArray(buffer.length) {
                buffer.readUInt8(it).toByte()
            }
        }
    }

    /**
     * Create a stream to read the file.
     * @see readText
     * @see readBytes
     */
    public fun readStream(encoding: String? = "utf8", emitClose: Boolean = true): ReadStream {
        requireFile()
        return fs.createReadStream(path, JsObject<fs.`T$50`> {
            this.emitClose = emitClose
            this.encoding = encoding
        })
    }

    /**
     * Append a line to this file (adds [lineSeparator] to [data]).
     * @see append
     */
    public fun appendLine(data: String, encoding: String = "utf8", createParents: Boolean = true) {
        append(data + OperatingSystem.lineSeparator, encoding, createParents)
    }

    /**
     * Append [data] to this file, creating it if it doesn't exist.
     */
    public fun append(data: String, encoding: String = "utf8", createParents: Boolean = true) {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "a"
        })
    }

    /**
     * Append [data] to this file, creating it if it doesn't exist.
     */
    public fun append(data: ByteArray, encoding: String = "utf8", createParents: Boolean = true) {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "a"
        })
    }

    /**
     * Create a stream to append to the file.
     * @see append
     */
    public fun appendStream(encoding: String? = "utf8", createParents: Boolean = true): WriteStream {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        return fs.createWriteStream(path, JsObject<fs.`T$51`> {
            this.encoding = encoding
            this.flags = "a"
        })
    }

    /**
     * Write to this file, truncating it if it exists, and creating it if not.
     */
    public fun write(data: String, encoding: String = "utf8", createParents: Boolean = true) {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "w"
        })
    }

    /**
     * Write to this file, truncating it if it exists, and creating it if not.
     */
    public fun write(data: ByteArray, encoding: String = "utf8", createParents: Boolean = true) {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "w"
        })
    }

    /**
     * Create a stream to write the file.
     * @see write
     */
    public fun writeStream(encoding: String? = "utf8", createParents: Boolean = true): WriteStream {
        if (createParents)
            parent.mkdir()

        requireFile(false)
        return fs.createWriteStream(path, JsObject<fs.`T$51`> {
            this.encoding = encoding
            this.flags = "w"
        })
    }

    override fun toString(): String {
        return path
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Path) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}