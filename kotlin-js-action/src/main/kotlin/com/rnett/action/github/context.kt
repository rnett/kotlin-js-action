package com.rnett.action.github

import NodeJS.WritableStream
import com.rnett.action.Path
import com.rnett.action.core.env
import com.rnett.action.glob.globFlow
import kotlinx.coroutines.flow.collect

public object github {
    public object context {
        public val eventName: String get() = internal.github.context.eventName
        public val sha: String get() = internal.github.context.sha
        public val ref: String get() = internal.github.context.ref
        public val workflow: String get() = internal.github.context.workflow
        public val action: String get() = internal.github.context.action
        public val actor: String get() = internal.github.context.actor
        public val job: String get() = internal.github.context.job
        public val runId: Int get() = internal.github.context.runId.toInt()
        public val runNumber: Int get() = internal.github.context.runNumber.toInt()
        public val workspace: String by env("GITHUB_WORKSPACE")
        public val workspacePath: Path get() = Path(workspace)

        public suspend fun hashFiles(patterns: List<String>, underWorkspace: Boolean = true): String {
            val result = crypto.createHash("sha256")
            val myWorkspacePath = workspacePath
            globFlow(patterns)
                .collect {
                    if (it.isFile && (!underWorkspace || it.isDescendantOf(myWorkspacePath))) {
                        val hash = crypto.createHash("sha256")
                        it.readStream().pipe(hash as WritableStream)
                        result.write(hash.digest().asDynamic())
                    }
                }
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            return result.digest("hex").toString()
        }
    }
}