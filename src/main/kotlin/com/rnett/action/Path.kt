package com.rnett.action

import com.rnett.action.io.io
import fs.*
import path.path as platformPath

/**
 * Path handling utilities modeled after Python's `pathlib`
 */
public class Path(rawPath: String, resolve: Boolean = true) {

    public val path: String = if (resolve) resolve(rawPath) else rawPath

    public fun resolve(): Path = Path(resolve(path))

    public companion object {
        public val cwd: Path get() = Path(".")

        public fun resolve(rawPath: String): String {
            val newRawPath = if(rawPath.startsWith("~")){
                platformPath.join(os.homedir(), rawPath.removePrefix("~"))
            } else
                rawPath

            return platformPath.resolve(platformPath.normalize(newRawPath))
        }

        public val seperator: String get() = platformPath.sep
    }

    public operator fun div(rest: String): Path = Path(
        platformPath.join(
            path,
            rest
        )
    )

    public operator fun div(rest: Path): Path = this / rest.path

    public fun descendant(rest: String): Path = this / rest
    public fun descendant(rest: Path): Path = this / rest

    public fun isDescendantOf(ancestor: Path): Boolean = this.path.startsWith(ancestor.path)
    public fun isDescendantOf(ancestor: String): Boolean = isDescendantOf(Path(ancestor))

    public val segments: List<String> by lazy { path.split(seperator).filter { it.isNotEmpty() } }

    public operator fun contains(segment: String): Boolean = segment in segments

    public val name: String by lazy { platformPath.basename(path) }

    public val extension: String by lazy { platformPath.extname(path) }

    public val parent: Path by lazy { Path(platformPath.dirname(path)) }

    /**
     * Repetedly gets [parent].  `ancestor(0)` is `this`, `ancestor(1)` is `parent`, and so on.
     */
    public fun ancestor(great: Int): Path {
        var current = this
        repeat(great){
            current = current.parent
        }
        return current
    }

    public val isAbsolute: Boolean by lazy { platformPath.isAbsolute(path) }

    public val exists: Boolean get() = fs.existsSync(path)

    public val stats: Stats? get() = if (exists) fs.lstatSync(path) else null

    public val isFile: Boolean get() = stats?.isFile() ?: false

    public val isDir: Boolean get() = stats?.isDirectory() ?: false

    public val isDirEmpty: Boolean get() = fs.readdirSync(path, JsObject<`T$38`> {
        this.withFileTypes = true
    }).isEmpty()

    public val children: List<Path>
        get() = fs.readdirSync(path, JsObject<`T$38`> {
            this.withFileTypes = true
        }).map { this / it.name }

    public fun requireFile() {
        if (!exists)
            error("File does not exist: $this")
        if (!isFile)
            error("Path $this is not a file, can't read")
    }

    public fun mkdir(parents: Boolean = true, existsOk: Boolean = true){
        if(exists){
            if(!existsOk)
                error("Path $path already exists")

            if(isFile)
                error("Path $path exists, but is a file")

            return
        }

        fs.mkdirSync(path, JsObject<MakeDirectoryOptions> {
            this.recursive = parents
        })

    }

    public suspend fun delete(recursive: Boolean = false) {
        if (!recursive && isDir && !isDirEmpty)
            error("Can't delete directory $path, it is not empty")

        io.rmRF(path)
    }

    public suspend fun copy(dest: String, recursive: Boolean = true, force: Boolean = true){
        io.cp(path, dest, recursive, force)
    }

    public suspend fun move(dest: String, force: Boolean = true){
        io.mv(path, dest, force)
    }

    public fun read(encoding: String = "utf8"): String {
        requireFile()
        return fs.readFileSync(path, JsObject<`T$43`> {
            this.encoding = encoding
        })
    }

    public fun appendLine(data: String, encoding: String = "utf8") {
        append(data + lineSeperator, encoding)
    }

    public fun append(data: String, encoding: String = "utf8") {
        requireFile()
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "a"
        })
    }

    public fun write(data: String, encoding: String = "utf8") {
        requireFile()
        fs.writeFileSync(path, data, JsObject<`T$45`> {
            this.encoding = encoding
            flag = "w"
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