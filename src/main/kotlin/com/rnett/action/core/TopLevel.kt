package com.rnett.action.core

public fun String.maskSecret(): Unit = maskSecret(this)

public fun maskSecret(secret: String): Unit = core.setSecret(secret)