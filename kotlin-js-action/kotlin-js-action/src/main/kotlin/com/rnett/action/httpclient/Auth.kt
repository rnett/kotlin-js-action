package com.rnett.action.httpclient

import com.rnett.action.encodeBase64

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