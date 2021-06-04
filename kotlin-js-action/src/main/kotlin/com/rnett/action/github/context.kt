package com.rnett.action.github

import NodeJS.ReadWriteStream
import NodeJS.WritableStream
import com.rnett.action.JsObject
import com.rnett.action.Path
import com.rnett.action.core.env
import com.rnett.action.glob.globFlow
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import stream.internal.Companion.pipeline

public object github {
    public object context {
        public val eventName: String by env("GITHUB_EVENT_NAME")
        public val sha: String by env("GITHUB_SHA")
        public val ref: String by env("GITHUB_REF")
        public val workflow: String by env("GITHUB_WORKFLOW")
        public val action: String by env("GITHUB_ACTION")
        public val actor: String by env("GITHUB_ACTOR")
        public val job: String by env("GITHUB_JOB")
        public val workspace: String by env("GITHUB_WORKSPACE")
        public val workspacePath: Path get() = Path(workspace)

        public suspend fun hashFiles(patterns: List<String>, underWorkspace: Boolean = true): String = coroutineScope {
            val result = crypto.createHash("sha256")
            val myWorkspacePath = workspacePath
            globFlow(patterns)
                .collect {
                    if (it.isFile && (!underWorkspace || it.isDescendantOf(myWorkspacePath))) {
                        val hash = crypto.createHash("sha256")
                        val read = fs.createReadStream(it.path, JsObject<fs.`T$50`> { })

                        launch {
                            read.pipe(hash as WritableStream)
                        }.join()

                        result.write(hash.digest())
                    }
                }
            return@coroutineScope result.digest("hex")
        }
    }
}