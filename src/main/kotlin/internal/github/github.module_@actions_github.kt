@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/github")
@file:JsNonModule

package internal.github

import kotlin.js.*

internal external var context: Context

internal external fun getOctokit(token: String, options: OctokitOptions = definedExternally): InstanceType<Any>