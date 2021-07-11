# Changelog

# Next

* Update serialization to `1.2.2` and coroutines to `1.5.1`.
* Add wrappers for `@actions/tool-cache`
* Add `Path.deleteRecursively()`.

# 1.4.0

Slightly breaking:

* Restructure HttpClient types. Some classes were replaced by interfaces or typealiases, but breakage should be pretty small unless you used the Json
  client or the legacy json request methods.
* Make the auto-build workflow use the `build` task, by default.

Non-breaking:

* Add a plugin method to add a task to generate the auto-build workflow.
* Builtin withDefault delegate methods

# 1.3.0

Many breaking changes, mostly around `env`, `Path`, `inputs`, and `state`. The whole library has been cleaned up and tested.

Breaking:

* **Made `execCapture` methods error on a command error return by default**.
* Cache client is properly marked as experimental
* `core.getInput` is removed in favor of explicit `getRequiredInput` or `getOptionalInput`.
* `inputs`, `env`/`exportEnv`, and `state` had their delegation reworked. Should be compatible for basic use cases except `env`, where the delegates
  became optional by default.
* Lots of type conversion methods were added for the reworked delegates
* `log` was renamed to `logger`.
* Updated underlying `exec` bindings, used new capture method.
* Fixed `execShell` shell escaping.
* Removed `hashFiles` from `github.context`, added a wrapper for the underlying method to the `glob` package.
* Updated the underlying `glob` package, added `matchDirectories` option and `hashFiles`.
* Removed `currentOS` and `lineSeperator` in favor of `OperatingSystem.current`/`.lineSeperator`, added and moved other properties
  to `OperatingSystem` companion.
* Made `Path.cwd` settable (it calls `cd`).
* Make `Path` read, write, and append methods `suspend` by default, added non-suspending versions.
* Move `Path.seperator` to `OperatingSystem.pathSeperator`.
* Reworked the `Path.copy` and `Path.move` methods, seperate out `*Into` and `*ChildrenInto`.
* Replace `Path.read` with `Path.readText`, add `Path.readBytes`.

Non-breaking:

* Add stream utils
* Add Buffer <-> ByteArray conversions
* Add tests
* Added wrappers for `@actions/http-client`. Used via `HttpClient` or `JsonHttpClient` in the `serialization` artifact.
* Add serialization artifact for Kotlinx serialization support
* Added `runAction`, like `runOrFail` but returns `Unit`.
* Add `Buffer` and `ByteArray` `Path` write methods.
