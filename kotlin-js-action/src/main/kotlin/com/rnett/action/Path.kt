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
        public val cwd: Path get() = Path(currentProcess.cwd())

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

        /**
         * Get the current OS's path seperator
         */
        public val seperator: String get() = platformPath.sep
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
    public val segments: List<String> by lazy { path.split(seperator).filter { it.isNotEmpty() } }

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
     * Throw if this path is not a file, or doesn't exist.
     *
     * @return this
     */
    public fun requireFile(requireExists: Boolean = true): Path {
        if (!requireExists && !exists)
            return this
        if (!exists)
            error("File does not exist: $this")
        if (!isFile)
            error("Path $this is not a file")
        return this
    }

    /**
     * Throw if this path is not a directory, or doesn't exist.
     *
     * @return this
     */
    public fun requireDirectory(requireExists: Boolean = true): Path {
        if (!requireExists && !exists)
            return this
        if (!exists)
            error("Directory does not exist: $this")
        if (!isDir)
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
     */
    public fun mkdir(parents: Boolean = true, existsOk: Boolean = true) {
        if (exists) {
            if (!existsOk)
                error("Path $path already exists")

            if (isFile)
                error("Path $path exists, but is a file")

            return
        }

        fs.mkdirSync(path, JsObject<MakeDirectoryOptions> {
            this.recursive = parents
        })

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
     * Copy this file to a new location, recursively by default.
     *
     * @see io.cp
     */
    public suspend fun copy(dest: Path, recursive: Boolean = true, force: Boolean = true) {
        io.cp(path, dest, recursive, force)
    }

    /**
     * Copy this directory's children into the directory [dest], creating it if it does not exist.
     */
    public suspend fun copyChildren(dest: Path, force: Boolean = true) {
        requireDirectory()
        dest.requireDirectory(false)
        if (!dest.exists)
            dest.mkdir()
        children.forEach { it.copy(dest, force) }
    }

    /**
     * Move this file to a new location.
     *
     * @see io.mv
     */
    public suspend fun move(dest: Path, force: Boolean = true) {
        io.mv(path, dest, force)
    }

    /**
     * Move this directory's children into the directory [dest], creating it if it does not exist.
     */
    public suspend fun moveChildren(dest: Path, force: Boolean = true) {
        requireDirectory()
        dest.requireDirectory(false)
        if (!dest.exists)
            dest.mkdir()
        children.forEach { it.move(dest, force) }
    }

    /**
     * Copy this file to a new location, recursively by default.
     *
     * @see io.cp
     */
    public suspend fun copy(dest: String, recursive: Boolean = true, force: Boolean = true) {
        io.cp(path, dest, recursive, force)
    }

    /**
     * Move this directory's children into the directory [dest], creating it if it does not exist.
     */
    public suspend fun copyChildren(dest: String, force: Boolean = true): Unit = copyChildren(Path(dest), force)

    /**
     * Move this file to a new location.
     *
     * @see io.mv
     */
    public suspend fun move(dest: String, force: Boolean = true) {
        io.mv(path, dest, force)
    }

    /**
     * Move this directory's children into the directory [dest], creating it if it does not exist.
     */
    public suspend fun moveChildren(dest: String, force: Boolean = true): Unit = moveChildren(Path(dest), force)

    /**
     * Read this file.
     */
    public fun read(encoding: String = "utf8"): String {
        requireFile()
        return fs.readFileSync(path, JsObject<`T$43`> {
            this.encoding = encoding
        })
    }

    /**
     * Create a stream to read the file.
     * @see read
     */
    public fun readStream(encoding: String = "utf8"): ReadStream {
        requireFile()
        return fs.createReadStream(path, JsObject<fs.`T$50`> {
            this.encoding = encoding
        })
    }

    /**
     * Append a line to this file (adds [lineSeparator] to [data]).
     * @see append
     */
    public fun appendLine(data: String, encoding: String = "utf8") {
        append(data + lineSeparator, encoding)
    }

    /**
     * Append [data] to this file, creating it if it doesn't exist.
     */
    public fun append(data: String, encoding: String = "utf8") {
        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "a"
        })
    }

    /**
     * Create a stream to read the file.
     * @see append
     */
    public fun appendStream(encoding: String = "utf8"): WriteStream {
        requireFile(false)
        return fs.createWriteStream(path, JsObject<fs.`T$51`> {
            this.encoding = encoding
            this.flags = "a"
        })
    }

    /**
     * Write to this file, truncating it if it exists, and creating it if not.
     */
    public fun write(data: String, encoding: String = "utf8") {
        requireFile(false)
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "w"
        })
    }

    /**
     * Create a stream to read the file.
     * @see write
     */
    public fun writeStream(encoding: String = "utf8"): WriteStream {
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