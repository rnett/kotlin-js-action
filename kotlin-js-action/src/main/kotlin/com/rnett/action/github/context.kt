package com.rnett.action.github

import com.rnett.action.Path
import com.rnett.action.core.env

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
    }
}