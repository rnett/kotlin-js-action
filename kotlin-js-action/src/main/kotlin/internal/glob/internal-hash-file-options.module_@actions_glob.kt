@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

package internal.glob

internal external interface HashFileOptions {
    var followSymbolicLinks: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}