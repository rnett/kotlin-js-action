# Kotlin JS GitHub Action SDK

![Maven Central](https://img.shields.io/maven-central/v/com.github.rnett.ktjs-github-action/kotlin-js-action)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.rnett.ktjs-github-action/kotlin-js-action?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/github/rnett/ktjs-github-action/)

### [Docs](https://rnett.github.io/kotlin-js-action/release/-kotlin%20-j-s%20-git-hub%20-action%20-s-d-k)

[For latest SNAPSHOT build](https://rnett.github.io/kotlin-js-action/snapshot/-kotlin%20-j-s%20-git-hub%20-action%20-s-d-k/)

Kotlin JS utilities for writing GitHub Actions, including wrappers
for [actions/toolkit](https://github.com/actions/toolkit) packages, except for `@actions/github`
and `@actions/tool-cache`.  `@actions/tool-cache` will be added once blockind `dukat` bugs are fixes.

Many methods are `suspend`, using a `suspend` `main` is recomended.

Code is mostly untested, as are the underlying `kotlinx-nodejs` bindings. However, the parts that have been tested imply
the rest "should" work.

In addition to the `@actions` bindings, a utility `Path` class modeled after Python's `pathlib` is included, as are some
miscellaneous utilities (like `JsObject` and an OS enum and detector).

If you need multiple executables for different entrypoints, i.e. for pre and post phases, I
recommend [turansky/kfc-plugins](https://github.com/turansky/kfc-plugins#multiple-outputs), as it is not yet supported
natively in Kotlin JS.
