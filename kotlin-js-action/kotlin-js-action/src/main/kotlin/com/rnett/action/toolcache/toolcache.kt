package com.rnett.action.toolcache

import com.rnett.action.OperatingSystem
import com.rnett.action.Path
import com.rnett.action.core.PATH
import com.rnett.action.core.logger
import com.rnett.action.httpclient.HeaderProvider
import com.rnett.action.httpclient.toIHeaders
import internal.toolcache.IToolRelease
import kotlinx.coroutines.await

// TODO make value class
public data class VersionedTool(val tool: Path, val version: String)

/**
 * Wrappers for [`@actions/tool-cache`](https://github.com/actions/toolkit/tree/main/packages/tool-cache).
 */
public object toolcache {

    /**
     * Download a tool from an url and stream it into a file
     *
     * @param url url of tool to download
     * @param dest path to download tool.  Will be a temp directory if `null`.
     * @param auth authorization header
     * @param headers other headers
     * @returns path to downloaded tool
     */
    public suspend fun downloadTool(url: String, dest: Path? = null, auth: String? = null, headers: HeaderProvider = HeaderProvider { }): Path =
        internal.toolcache.downloadTool(url, dest?.path, auth, headers.toIHeaders()).await().let(::Path)


    /**
     * Extract a .7z file
     *
     * **WINDOWS ONLY**
     *
     * @param file     path to the .7z file
     * @param dest     destination directory.  Will be a temp directory if `null`.
     * @param _7zPath  path to 7zr.exe. Optional, for long path support. Most .7z archives do not have this
     * problem. If your .7z archive contains very long paths, you can pass the path to 7zr.exe which will
     * gracefully handle long paths. By default 7zdec.exe is used because it is a very small program and is
     * bundled with the tool lib. However it does not support long paths. 7zr.exe is the reduced command line
     * interface, it is smaller than the full command line interface, and it does support long paths. At the
     * time of this writing, it is freely available from the LZMA SDK that is available on the 7zip website.
     * Be sure to check the current license agreement. If 7zr.exe is bundled with your action, then the path
     * to 7zr.exe can be pass to this function.
     * @returns        path to the destination directory
     */
    public suspend fun extract7z(file: Path, dest: Path? = null, _7zPath: Path? = null): Path =
        internal.toolcache.extract7z(file.path, dest?.path, _7zPath?.path).await().let(::Path)


    /**
     * Extract a compressed tar archive
     *
     * @param file     path to the tar
     * @param dest     destination directory.  Will be a temp directory if `null`.
     * @param flags    flags for the tar command to use for extraction. Defaults to 'xz' (extracting gzipped tars). Optional.
     * @returns        path to the destination directory
     */
    public suspend fun extractTar(file: Path, dest: Path? = null, vararg flags: String = arrayOf("xz")): Path =
        internal.toolcache.extractTar(file.path, dest?.path, flags as Array<String>).await().let(::Path)


    /**
     * Extract a xar compatible archive
     *
     * **MAC ONLY**
     *
     * @param file     path to the archive
     * @param dest     destination directory. Will be a temp directory if `null`.
     * @param flags    flags for the xar. Optional.
     * @returns        path to the destination directory
     */
    public suspend fun extractXar(file: Path, dest: Path? = null, vararg flags: String): Path =
        internal.toolcache.extractXar(file.path, dest?.path, flags as Array<String>).await().let(::Path)


    /**
     * Extract a zip
     *
     * @param file     path to the zip
     * @param dest     destination directory. Will be a temp directory if `null`.
     * @returns        path to the destination directory
     */
    public suspend fun extractZip(file: Path, dest: Path? = null): Path =
        internal.toolcache.extractZip(file.path, dest?.path).await().let(::Path)


    /**
     * Extract an archive, looking at the file extension and using existing autodetection capabilities (i.e. `tar -a`) to determine the format.
     *
     * Tested on everything in [here](https://github.com/rnett/kotlin-js-action/tree/main/kotlin-js-action/src/test/resources/archives):
     * * `.7z` (Windows only)
     * * `.tar`
     * * `.zip`
     * * `.tar.gz`
     * * `.tar.bz2`
     *
     * @param file     path to the archive
     * @param dest     destination directory. Will be a temp directory if `null`.
     */
    public suspend fun extract(file: Path, dest: Path? = null): Path {
        require(file.exists) { "File $file does not exist" }
        return when {
            file.name.endsWith(".7z") -> extract7z(file, dest)
            file.name.endsWith(".xar") -> extractXar(file, dest)
            file.name.endsWith(".zip") -> extractZip(file, dest)
            else -> {
                logger.info("Could not detect archive format of $file, trying tar")
                if (OperatingSystem.isLinux)
                    extractTar(file, dest, "xa")
                else
                    extractTar(file, dest, "x")
            }
        }
    }

    /**
     * Caches a directory and installs it into the tool cacheDir.
     *
     * The cache will be keyed on [tool], [version], and [arch].
     *
     * @param sourceDir    the directory to cache into tools
     * @param tool          tool name
     * @param version       version of the tool.  semver format
     * @param arch          architecture of the tool.  Optional.  Defaults to machine architecture
     */
    public suspend fun cacheDir(sourceDir: Path, tool: String, version: String, arch: String? = null): Path =
        internal.toolcache.cacheDir(sourceDir.path, tool, version, arch).await().let(::Path)


    /**
     * Caches a downloaded file (GUID) and installs it
     * into the tool cache with a given targetName
     *
     * The cache will be keyed on [tool], [version], and [arch], and the file will be put at [targetFile] inside that cache.
     *
     * @param sourceFile    the file to cache into tools.  Typically a result of downloadTool which is a guid.
     * @param tool          tool name
     * @param version       version of the tool.  semver format
     * @param arch          architecture of the tool.  Optional.  Defaults to machine architecture
     * @param targetFile    the name of the file name in the tools directory.  Defaults to [sourceFile]'s name.
     */
    public suspend fun cacheFile(sourceFile: Path, tool: String, version: String, arch: String? = null, targetFile: String = sourceFile.name): Path =
        internal.toolcache.cacheFile(sourceFile.path, targetFile, tool, version, arch).await().let(::Path)


    /**
     * Finds the path to a tool version in the local installed tool cache, or `null` if no matching versions were found.
     *
     * @param toolName      name of the tool
     * @param versionSpec   version of the tool
     * @param arch          optional arch.  defaults to arch of computer
     */
    public fun find(toolName: String, versionSpec: String, arch: String? = null): Path? =
        internal.toolcache.find(toolName, versionSpec, arch).ifBlank { null }?.let(::Path)


    /**
     * Finds all versions of a tool that are installed in the local tool cache
     *
     * @param toolName  name of the tool
     * @param arch      optional arch.  defaults to arch of computer
     * @return the cached versions
     */
    public fun findAllVersions(toolName: String, arch: String? = null): List<String> =
        internal.toolcache.findAllVersions(toolName, arch).toList()

    /**
     * Finds all versions and paths of a tool that are installed in the local tool cache
     *
     * @param toolName  name of the tool
     * @param arch      optional arch.  defaults to arch of computer
     * @return the cached versions and their paths
     */
    public fun findAll(toolName: String, arch: String? = null): List<VersionedTool> = findAllVersions(toolName, arch).mapNotNull { version ->
        find(toolName, version, arch)?.let { VersionedTool(it, version) }
    }

    public suspend fun getManifestFromRepo(owner: String, repo: String, auth: String? = null, branch: String = "master"): List<IToolRelease> =
        internal.toolcache.getManifestFromRepo(owner, repo, auth, branch).await().toList()

    public suspend fun findFromManifest(
        versionSpec: String,
        stable: Boolean,
        manifest: List<IToolRelease>,
        archFilter: String = os.arch()
    ): IToolRelease? =
        internal.toolcache.findFromManifest(versionSpec, stable, manifest.toTypedArray(), archFilter).await()

    /**
     * Check if version string is explicit
     *
     * @param versionSpec      version string to check
     */
    public fun isExplicitVersion(versionSpec: String): Boolean = internal.toolcache.isExplicitVersion(versionSpec)

    /**
     * Get the highest satisfiying semantic version in `versions` which satisfies `versionSpec`, or `null` if there isn't one.
     *
     * @param versions        array of versions to evaluate
     * @param versionSpec     semantic version spec to satisfy
     */
    public fun evaluateVersions(versions: List<String>, versionSpec: String): String? =
        internal.toolcache.evaluateVersions(versions.toTypedArray(), versionSpec).ifBlank { null }

    /**
     * Load the tool from cache if it is cached, otherwise download and cache it.
     *
     * [download] is passed [version].  It should return a ready to use tool,
     * with any necessary extraction or setup done.
     *
     * @param name the tool's name
     * @param version the tool's version
     * @param arch the arch to get, or `null` to use the current arch
     * @param addToPath whether to add the gotten tool to PATH
     * @param download download and extract the tool
     */
    public suspend inline fun load(
        name: String,
        version: String,
        arch: String? = null,
        addToPath: Boolean = false,
        download: (version: String) -> Path
    ): Path {
        val toolPath = find(name, version, arch) ?: download(version).also {
            cacheDir(it, name, version, arch)
        }
        if (addToPath) {
            PATH += toolPath
        }
        return toolPath
    }
}