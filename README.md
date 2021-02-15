# Kotlin JS GitHub Action SDK

![Maven Central](https://img.shields.io/maven-central/v/com.github.rnett.ktjs-github-action/kotlin-js-action)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.rnett.ktjs-github-action/kotlin-js-action?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/github/rnett/ktjs-github-action/)

### [Docs](https://rnett.github.io/kotlin-js-action/release/-kotlin%20-j-s%20-git-hub%20-action%20-s-d-k)

[For latest SNAPSHOT build](https://rnett.github.io/kotlin-js-action/snapshot/-kotlin%20-j-s%20-git-hub%20-action%20-s-d-k/)

Kotlin JS utilities for writing GitHub Actions, including wrappers
for [actions/toolkit](https://github.com/actions/toolkit) packages, except for `@actions/github`
and `@actions/tool-cache`.  `@actions/tool-cache` will be added once blockind `dukat` bugs are fixes.

## Gradle Plugin

A rather hacky gradle plugin `com.github.rnett.ktjs-github-action` is also provided to configure Kotlin JS with bundling
for GitHub Actions, since the Kotlin JS plugin doesn't support bundling NodeJS.

There are two functions you can call in your build script:

* `com.rnett.action.githubAction`, called in the `js` target block to configure the target for GitHub actions. It
  configures a browser target, but with node dependencies in WebPack, and adds a task to generate the custom webpack
  config. The default kotlin tasks may break sometimes, but `build` will work.
* `com.rnett.action.useAutoBuildWorkflow`, called anywhere. Adds a task to generate a GitHub actions workflow for the
  project to build and commit the distributable on push, in case it wasn't built locally. This keeps it up to date with your latest
  changes.  Adds itself as a dependency of `wrapper` and `build` so it should always be present.

## Utility notes

Many of the utility methods are `suspend`, using a `suspend` `main` is recommended.

Code is mostly untested, as are the underlying `kotlinx-nodejs` bindings. However, the parts that have been tested imply
the rest "should" work.

In addition to the `@actions` bindings, a utility `Path` class modeled after Python's `pathlib` is included, as are some
miscellaneous utilities (like `JsObject` and an OS enum and detector).

## Examples
https://github.com/rnett/find-regex

https://github.com/rnett/publish-docs

https://github.com/rnett/import-gpg-key
