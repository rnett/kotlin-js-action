package com.rnett.action.httpclient

import NodeJS.get
import NodeJS.set
import com.rnett.action.JsObject
import com.rnett.action.jsEntries
import http.ClientRequestArgs
import http.OutgoingHttpHeaders

internal fun Any?.headerToString() = when (val value = this) {
    null -> null
    is String -> value
    is Array<*> -> value.joinToString(",")
    else -> value.toString()
}

/**
 * An object or lambda that provides some headers.
 */
public fun interface HeaderProvider : RequestHandler {
    public fun MutableHeaders.headers()
    override fun ClientRequestArgs.prepareRequest(headers: MutableHeaders) {
        headers.headers()
    }
}

public operator fun Map<String, String>.plus(header: HeaderProvider): Map<String, String> =
    this + MapHeaders().apply { +header }.toMap()

public fun HeaderProvider.toMap(): Map<String, String> = MapHeaders().apply { headers() }.toMap()

public sealed interface Headers {

    /**
     * Whether the value of [name] is set.
     */
    public operator fun contains(name: String): Boolean = get(name) != null

    /**
     * Get the value of a header.
     */
    public operator fun get(name: String): String?

    public fun toMap(): Map<String, String>
}

/**
 * An interface allowing the setting of headers.
 */
public sealed interface MutableHeaders : Headers {

    /**
     * Set a header.
     */
    public operator fun set(name: String, value: String)

    /**
     * Add a header, adding it to the existing header after a `,` if a this header has already been set.
     */
    public fun add(name: String, value: String) {
        val current = this[name]
        if (current.isNullOrBlank()) {
            this[name] = value
        } else {
            this[name] = "$current,$value"
        }
    }

    /**
     * Add a header.
     * @see add
     */
    public operator fun plusAssign(header: Pair<String, String>): Unit = add(header.first, header.second)

    /**
     * Add headers from a provider.
     */
    public operator fun plusAssign(other: HeaderProvider) {
        other.apply {
            headers()
        }
    }

    /**
     * Add headers from a provider.
     */
    public operator fun HeaderProvider.unaryPlus() {
        this@MutableHeaders += this
    }

    /**
     * Set multiple headers.
     * @see set
     */
    public infix fun setFrom(other: Map<String, String>) {
        other.forEach { (k, v) -> this[k] = v }
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public infix fun addFrom(other: Map<String, String>) {
        other.forEach { (k, v) -> add(k, v) }
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public operator fun plusAssign(other: Map<String, String>) {
        addFrom(other)
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public operator fun Map<String, String>.unaryPlus() {
        this@MutableHeaders += this
    }

    /**
     * Set multiple headers.
     * @see set
     */
    public infix fun setFrom(other: Iterable<Pair<String, String>>) {
        other.forEach { (k, v) -> this[k] = v }
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public infix fun addFrom(other: Iterable<Pair<String, String>>) {
        other.forEach { (k, v) -> add(k, v) }
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public operator fun plusAssign(other: Iterable<Pair<String, String>>) {
        addFrom(other)
    }

    /**
     * Add multiple headers.
     * @see add
     */
    public operator fun Iterable<Pair<String, String>>.unaryPlus() {
        this@MutableHeaders += this
    }
}

internal class MapHeaders private constructor(private val map: MutableMap<String, String> = mutableMapOf()) :
    MutableHeaders {
    constructor(map: Map<String, String> = emptyMap()) : this(map.mapKeysTo(mutableMapOf()) { it.key.lowercase() })

    override fun get(name: String): String? = map[name.lowercase()]

    override fun toMap(): Map<String, String> = map

    override fun set(name: String, value: String) {
        map[name.lowercase()] = value
    }
}

internal class OutgoingJsHeaders(private val internal: ClientRequestArgs) : MutableHeaders {

    private val internalHeaders: OutgoingHttpHeaders
        get() {
            if (internal.headers == null) {
                internal.headers = JsObject()
            }
            return internal.headers!!
        }

    override fun get(name: String): String? = internalHeaders[name].unsafeCast<Any?>().headerToString()

    override fun set(name: String, value: String) {
        internalHeaders[name.lowercase()] = value
    }

    override fun toMap(): Map<String, String> =
        jsEntries(internalHeaders).mapNotNull { (key, value) ->
            value.unsafeCast<Any?>().headerToString()?.let { key to it }
        }.toMap()
}