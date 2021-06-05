@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)
@file:JsModule("@actions/github")
@file:JsNonModule

package internal.github

internal external open class Context {
    open var payload: WebhookPayload
    open var eventName: String
    open var sha: String
    open var ref: String
    open var workflow: String
    open var action: String
    open var actor: String
    open var job: String
    open var runNumber: Number
    open var runId: Number
    open var apiUrl: String
    open var serverUrl: String
    open var graphqlUrl: String
}