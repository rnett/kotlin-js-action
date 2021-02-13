package com.rnett.action.glob

import com.rnett.action.JsObject
import com.rnett.action.Path
import internal.glob.create
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tsstdlib.IteratorYieldResult
import kotlin.js.Promise
import internal.glob.Globber as JsGlobber

/**
 * A globber instance for a given glob.
 */
public class Globber internal constructor(private val _globber: JsGlobber) {

    /**
     * Get all matching files.
     *
     * For very large globs, [globFlow] is recommended.
     */
    public suspend fun glob(): List<Path> = _globber.glob().await().map { Path(it) }

    /**
     * Get all matching files, asynchronously.  Good for very large globs.
     */
    public suspend fun globFlow(): Flow<Path> = flow {
        val iterator = _globber.globGenerator()
        do {
            val next = (iterator.next() as Promise<IteratorYieldResult<String>>).await()
            emit(Path(next.value))
        } while (next.done == false)
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
 * See [`@actions/glob`'s docs](https://github.com/actions/toolkit/tree/main/packages/glob#patterns)
 *
 * @param patterns patterns to check
 * @param followSymbolicLinks whether to follow symbolic links when collecting files
 * @param implicitDescendants whether to implicitly include all descendants of matching files
 * @param omitBrokenSymbolicLinks ignore broken symbolic links
 */
public suspend fun Globber(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true
): Globber {
    return Globber(create(patterns.joinToString("\n"), JsObject {
        this.followSymbolicLinks = followSymbolicLinks
        this.implicitDescendants = implicitDescendants
        this.omitBrokenSymbolicLinks = omitBrokenSymbolicLinks
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
 */
public suspend fun glob(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true
): List<Path> = Globber(
    *patterns,
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks
).glob()

/**
 * Get all files matching [patterns], asynchronously.  Good for very large globs.
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
 */
public suspend fun globFlow(
    vararg patterns: String,
    followSymbolicLinks: Boolean = true,
    implicitDescendants: Boolean = true,
    omitBrokenSymbolicLinks: Boolean = true
): Flow<Path> = Globber(
    *patterns,
    followSymbolicLinks = followSymbolicLinks,
    implicitDescendants = implicitDescendants,
    omitBrokenSymbolicLinks = omitBrokenSymbolicLinks
).globFlow()