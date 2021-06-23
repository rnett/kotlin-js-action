package com.rnett.action.httpclient

import Buffer

public fun String.encodeBase64(): String = Buffer.from(this).toString("base64")

public fun String.decodeBase64(): String = Buffer.from(this, "base64").toString("utf8")

/**
 * Request handler for basic auth.
 */
public data class BasicAuthHandler(val username: String, val password: String) : HeaderProvider {
    override fun MutableHeaders.headers() {
        this["Authorization"] = "Basic ${"$username:$password".encodeBase64()}"
    }
}

/**
 * Request handler for bearer auth.
 */
public data class BearerAuthHandler(val token: String) : HeaderProvider {
    override fun MutableHeaders.headers() {
        this["Authorization"] = "Bearer $token"
    }
}

/**
 * Request handler for personal access token auth.
 */
public data class PersonalAccessTokenAuthHandler(val token: String) : HeaderProvider {
    override fun MutableHeaders.headers() {
        this["Authorization"] = "Basic ${"PAT:$token".encodeBase64()}"
    }
}