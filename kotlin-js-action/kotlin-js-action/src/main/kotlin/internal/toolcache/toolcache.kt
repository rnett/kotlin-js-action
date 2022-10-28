@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/tool-cache")
@file:JsNonModule

package internal.toolcache

import internal.httpclient.IHeaders
import kotlin.js.Promise

internal external fun downloadTool(
    url: String,
    dest: String? = definedExternally,
    auth: String? = definedExternally,
    headers: IHeaders? = definedExternally
): Promise<String>

internal external fun extract7z(
    file: String,
    dest: String? = definedExternally,
    _7zPath: String? = definedExternally,
): Promise<String>

internal external fun extractTar(
    file: String,
    dest: String? = definedExternally,
    flags: Array<String> = definedExternally,
): Promise<String>

internal external fun extractXar(
    file: String,
    dest: String? = definedExternally,
    flags: Array<String> = definedExternally,
): Promise<String>

internal external fun extractZip(
    file: String,
    dest: String? = definedExternally,
): Promise<String>

internal external fun cacheDir(sourceDir: String, tool: String, version: String, arch: String? = definedExternally): Promise<String>

internal external fun cacheFile(
    sourceFile: String,
    targetFile: String,
    tool: String,
    version: String,
    arch: String? = definedExternally
): Promise<String>

internal external fun find(toolName: String, versionSpec: String, arch: String? = definedExternally): String

internal external fun findAllVersions(toolName: String, arch: String? = definedExternally): Array<String>

public external interface IToolRelease {
    public val version: String
    public val stable: Boolean

    @JsName("release_url")
    public val releaseUrl: String
    public val files: Array<IToolReleaseFile>
}

public external interface IToolReleaseFile {
    public val filename: String
    public val platform: String

    @JsName("platform_version")
    public val platformVersion: String?
    public val arch: String

    @JsName("download_url")
    public val downloadUrl: String
}

internal external fun getManifestFromRepo(
    owner: String,
    repo: String,
    auth: String? = definedExternally,
    branch: String = definedExternally
): Promise<Array<IToolRelease>>

internal external fun findFromManifest(
    versionSpec: String,
    stable: Boolean,
    manifest: Array<IToolRelease>,
    archFilter: String = definedExternally
): Promise<IToolRelease?>

internal external fun isExplicitVersion(versionSpec: String): Boolean


internal external fun evaluateVersions(versions: Array<String>, versionSpec: String): String