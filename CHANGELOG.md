# Changelog

## Next

### Breaking

### Non-breaking

## 1.6.0

### Breaking

* Update to Node 16.18.0
* Switch from the obsolete `kotlinx-nodejs`
  to [`kotlin-wrappers/node`](https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-node/README.md).
  **This is a massively breaking change!**.
    * Numerous APIs have changed as a result of this, and many NodeJS types have changed.
    * This includes `Path`, which has changed significantly. There are no more non-`suspend` methods, and many properties became `suspend` methods.
    * It also gets rid of the `jcenter()` requirement :tada:
* Update Kotlin to 1.7.21
* Update Kotlinx.serialization to 1.4.1
* Update Kotlinx.coroutines to 1.6.4
* Update `@actions/http-client` to `2.0.1`

### Non-breaking

Note: Many of these supposedly non-breaking updates have had breaking changes in the same areas, due to the NodeJS wrapper change.

* Update `@actions/core` to `1.10.0`. This adds summary creation, accessible via the `summary` object.
* Update `@actions/exec` to `1.1.1`.
* Update `@actions/glob` to `0.3.0`.
* Update `@actions/io` to `1.1.2`.
* Update `@actions/tool-cache` to `2.0.1`.
* Update `@actions/github` to `5.1.1`.
* Update `@actions/artifact` to `1.1.0`.
* Update `@actions/cache` to `3.0.6`.
* Update `@actions/http-client` to `2.0.1`.

## 1.5.0

### Breaking

* Update Kotlin to 1.6.10
* Update Kotlinx coroutines to 1.6.0
* Update Kotlinx serialization to 1.3.2

### Non-breaking

* Update `@actions/artifact` to 0.6.1

## 1.4.3

### Non-breaking

* Update `@actions/core` to 1.5.0.
  * Add `notice` logging methods.
  * Add `AnnotationProperties` and annotation producing overloads for `notice`, `warning`, and `error`.

## 1.4.2

### Non-breaking

* Update Kotlin to 1.5.30.

## 1.4.1

### Non-breaking

* Update serialization to `1.2.2` and coroutines to `1.5.1`.
* Add wrappers for `@actions/tool-cache`
* Add `Path.deleteRecursively()`.
* Add `PATH += Path`.

## 1.4.0

### Slightly breaking

* Restructure HttpClient types. Some classes were replaced by interfaces or typealiases, but breakage should be pretty
  small unless you used the Json client or the legacy json request methods.
* Make the auto-build workflow use the `build` task, by default.

### Non-breaking

* Add a plugin method to add a task to generate the auto-build workflow.
* Builtin withDefault delegate methods

## 1.3.0

Many breaking changes, mostly around `env`, `Path`, `inputs`, and `state`. The whole library has been cleaned up and
tested.

### Breaking

* **Made `execCapture` methods error on a command error return by default**.
* Cache client is properly marked as experimental
* `core.getInput` is removed in favor of explicit `getRequiredInput` or `getOptionalInput`.
* `inputs`, `env`/`exportEnv`, and `state` had their delegation reworked. Should be compatible for basic use cases
  except `env`, where the delegates became optional by default.
* Lots of type conversion methods were added for the reworked delegates
* `log` was renamed to `logger`.
* Updated underlying `exec` bindings, used new capture method.
* Fixed `execShell` shell escaping.
* Removed `hashFiles` from `github.context`, added a wrapper for the underlying method to the `glob` package.
* Updated the underlying `glob` package, added `matchDirectories` option and `hashFiles`.
* Removed `currentOS` and `lineSeperator` in favor of `OperatingSystem.current`/`.lineSeperator`, added and moved other
  properties to `OperatingSystem` companion.
* Made `Path.cwd` settable (it calls `cd`).
* Make `Path` read, write, and append methods `suspend` by default, added non-suspending versions.
* Move `Path.seperator` to `OperatingSystem.pathSeperator`.
* Reworked the `Path.copy` and `Path.move` methods, seperate out `*Into` and `*ChildrenInto`.
* Replace `Path.read` with `Path.readText`, add `Path.readBytes`.

### Non-breaking

* Add stream utils
* Add Buffer <-> ByteArray conversions
* Add tests
* Added wrappers for `@actions/http-client`. Used via `HttpClient` or `JsonHttpClient` in the `serialization` artifact.
* Add serialization artifact for Kotlinx serialization support
* Added `runAction`, like `runOrFail` but returns `Unit`.
* Add `Buffer` and `ByteArray` `Path` write methods.
