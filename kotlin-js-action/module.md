# Module Kotlin JS GitHub Action SDK

Kotlin JS utilities for writing GitHub Actions, including wrappers
for [actions/toolkit](https://github.com/actions/toolkit) packages, except for `@actions/github`
and `@actions/tool-cache`.  `@actions/tool-cache` will be added once blocking `dukat` bugs are fixes.

In addition to the `@actions` bindings, a utility `Path` class modeled after Python's `pathlib` is included, as are some
miscellaneous utilities (like `JsObject` and an OS enum and detector).
