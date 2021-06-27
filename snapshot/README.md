# [Kotlin JS GitHub Action SDK](https://github.com/rnett/kotlin-js-action)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.rnett.ktjs-github-action/kotlin-js-action)](https://search.maven.org/artifact/com.github.rnett.ktjs-github-action/kotlin-js-action)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.rnett.ktjs-github-action/kotlin-js-action?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/github/rnett/ktjs-github-action/)

### Artifacts

* `com.github.rnett.ktjs-github-action:kotlin-js-action` - the SDK
* `com.github.rnett.ktjs-github-action:serialization` - Kotlinx serialization support for the SDK
* `com.github.rnett.ktjs-github-action` - the gradle plugin. The maven artifact
  is `com.github.rnett.ktjs-github-action:kotlin-js-action-plugin`
  if you are not using the `plugins` block.

### [Docs](https://rnett.github.io/kotlin-js-action/release/)

[For latest SNAPSHOT build](https://rnett.github.io/kotlin-js-action/snapshot/)

Kotlin JS utilities for writing GitHub Actions, including wrappers
for [actions/toolkit](https://github.com/actions/toolkit) packages, except for `@actions/github`
and `@actions/tool-cache`.  `@actions/tool-cache` will be added once blocking `dukat` bugs are fixes.

## SDK

The GitHub actions SDK provides wrappers for most of the [`actions/toolkit`](https://github.com/actions/toolkit)
packages, as well as some utilities for working with NodeJS types.

Actions should almost always have an entrypoint like:

```kotlin
suspend fun main() = runAction {

}
```

This ensures that any uncaught exceptions are properly reported to the Github runtime.

Generally, most of the wrappers are thin and can be fully understood by reading the docs. We explain the more
complicated ones below.

### Utils

Utilities include backpressure cognizant `Flow` <-> `Stream` conversions,
`WritableStream.writeSuspending` (which suspend for backpressure),
`Buffer.asByteArray()`, `jsEntries`, and an `external interface` object builder `JsObject`.

### Inputs, State, and Env

The `inputs`, `state`, and `env` (and `exportEnv`) objects provide action input, state, and environment accessors. They
can all be accessed by key, but also provide delegates. Delegates can delegate from the object directly, specify a name
with `invoke(String)`, or whether the value is required with `optional`/`optional(String)` and `required`
/`required(String)`.
`inputs` and `state` delegates are required by default and the objects have `optional` versions, where `env` is the
opposite.

`exportEnv`/`env.export` is another environment wrapper that exports set environment variables to the Github workflow.

### Typesafe delegates

Many objects, including `inputs`, `env`, and `outputs`, are accessed primarily by `String` delegates. However, this is
insufficient when accessing structured data, such as multi-line or boolean inputs or JSON formatted `state`.

To this end, we provide delegate (`ReadOnlyProperty` and `ReadWriteProperty`, to be exact) mapping functions in
the `delegates` package. You can write your own using `map`, `mapNonNull`, `ifNull`, etc, and we provide a large set of
default implementations including `isTrue` (~`toBoolean` in the stdlib),
`toBoolean` (~`toBooleanStrict` in the stdlib), `toInt`, `lines`, `trim`, and `lowercase` for both read-only and
read-write delegates.

This is also how serialization support is implemented. The `serialization` artifact provides `deserialize`
and `serialize` methods, so you can do something like `val myState by state.deserialize<MyState>()`.

### Shell Exec

`exec` is based on the `@actions/exec` package, however it has some issues with input redirection, in particular that
passing `outStream` will cause a `[command] $command` header to be written to the output file. To this end we provide
`execShell` commands that instead of executing the raw command, excuting it using something like `bash -c "$command"`.
Powershell (Windows) or bash are used by default. Note that powershell output redirects (`>`) will be written in
UTF-16-le with a BOM. Command strings passed here support things like `>`, `|`, aliases, and posix-like powershell
commands.

### HttpClient

The `httpclient` package wraps `@actions/http-client`.  `HttpClient` provides a lightweight http client, with the
ability to send, `Flow`s and text, and set default and per-request headers. While the typed result methods are provided,
typed requests should usually be handled via `JsonHttpClient` in the `serialization` artifact.

## Gradle Plugin

The gradle plugin (`com.github.rnett.ktjs-github-action`) can Kotlin JS with bundling for GitHub Actions, since the
Kotlin JS plugin doesn't support bundling NodeJS yet.

There are two functions you can call in your build script:

* `com.rnett.action.githubAction`, called in the `js` target block to configure the target for GitHub actions. It
  configures a browser target, but with node dependencies in Webpack, and adds a task to generate the custom webpack
  config. This replaces `browser` or `nodejs`.
* `com.rnett.action.generateAutoBuildWorkflow`, called anywhere. Generates a GitHub actions workflow for the project to
  build and commit the distributable on push, in case it wasn't built locally. This keeps it up to date with your latest
  changes. By default, if called with a `Project` receiver, generates it in `$rootDir/.github/workflows/`

## Examples

https://github.com/rnett/find-regex

https://github.com/rnett/publish-docs

https://github.com/rnett/import-gpg-key
