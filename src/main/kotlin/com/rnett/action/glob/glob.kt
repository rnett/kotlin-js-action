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

public class Globber internal constructor(private val _globber: JsGlobber) {
    public suspend fun glob(): List<Path> = _globber.glob().await().map { Path(it) }

    public suspend fun globFlow(): Flow<Path> = flow {
        val iterator = _globber.globGenerator()
        do {
            val next = (iterator.next() as Promise<IteratorYieldResult<String>>).await()
            emit(Path(next.value))
        } while (next.done == false)
    }
}

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