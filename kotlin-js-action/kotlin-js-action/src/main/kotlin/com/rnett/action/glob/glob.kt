package com.rnett.action.glob

import com.rnett.action.JsObject
import com.rnett.action.Path
import internal.glob.create
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.js.Promise
import internal.glob.Globber as JsGlobber

private external interface IteratorYieldResult<T> {
    val done: Boolean?
    val value: T
}

/**
 * A globber instance for a given glob.
 */
public class Globber internal constructor(private val _globber: JsGlobber) {

    /**
     * Get all matching files.
     * Order is nondeterministic.
     *
     * For very large globs, [globFlow] is recommended.
     */
    public suspend fun glob(): List<Path> = _globber.glob().await().map { Path(it) }

    /**
     * Get all matching files, asynchronously.  Good for very large globs.
     * Order is nondeterministic.
     */
    public suspend fun globFlow(): Flow<Path> = flow {
        val iterator = _globber.globGenerator()
        while (true) {
            val next = (iterator.next() as Promise<IteratorYieldResult<String>>).await()

            if (next.done != false)
                break

            emit(Path(next.value))
        }
    }
}

/**
 * Create a [Globber].
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * File ordering is nondeterministic.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun Globber(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): Globber {
    return Globber(create(patterns.joinToString("\n"), JsObject {
        this.followSymbolicLinks = followSymbolicLinks
        this.implicitDescendants = implicitDescendants
        this.omitBrokenSymbolicLinks = omitBrokenSymbolicLinks
        this.matchDirectories = matchDirectories
    }).await())
}

/**
 * Get all files matching [patterns].
 *
 * For very large globs, [globFlow] is recommended.
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun glob(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): List<Path> = Globber(
    *patterns,
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
    matchDirectories = matchDirectories
).glob()

/**
 * Get all files matching [patterns], asynchronously.  Good for very large globs.
 * Order is nondeterministic.
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun globFlow(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): Flow<Path> = Globber(
    *patterns,
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
    matchDirectories = matchDirectories
).globFlow()

/**
 * Create a [Globber].
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun Globber(
    patterns: List<String>,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): Globber = Globber(
    *patterns.toTypedArray(),
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
    matchDirectories = matchDirectories
)

/**
 * Get all files matching [patterns].
 * Order is nondeterministic.
 *
 * For very large globs, [globFlow] is recommended.
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun glob(
    patterns: List<String>,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): List<Path> = glob(
    *patterns.toTypedArray(),
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
    matchDirectories = matchDirectories
)

/**
 * Get all files matching [patterns], asynchronously.  Good for very large globs.
 * Order is nondeterministic.
 *
 * `*`, `?`, `[...]`, and `**` are supported in [patterns].  `~` will be expanded.
 * Patterns that begin with `#` are ignored.
 * Patterns that begin with `!` exclude matching files.
 * Characters can be escaped by wrapping them in `[]`, or by using `\` on non-Windows systems.
 *
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 * @param matchDirectories whether to include directories in the result
 */
public suspend fun globFlow(
    patterns: List<String>,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true,
    matchDirectories: Boolean = true
): Flow<Path> = globFlow(
    *patterns.toTypedArray(),
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks,
    matchDirectories = matchDirectories
)

/**
 * Hash files, the same as the actions context function `hashFiles.
 *
 * Note that patterns are relative to `GITHUB_WORKSPACE` and only include files inside of it.
 *
 * @param followSymbolicLinks whether to follow symbolic links when hashing files
 */
public suspend fun hashFiles(patterns: List<String>, followSymbolicLinks: Boolean = true): String =
    internal.glob.hashFiles(patterns.joinToString("\n"), JsObject {
        this.followSymbolicLinks = followSymbolicLinks
    }).await()