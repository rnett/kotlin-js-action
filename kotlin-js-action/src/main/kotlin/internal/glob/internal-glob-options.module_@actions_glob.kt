@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/glob")
@file:JsNonModule

package internal.glob

internal external interface GlobOptions {
    var followSymbolicLinks: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var implicitDescendants: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var matchDirectories: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var omitBrokenSymbolicLinks: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}