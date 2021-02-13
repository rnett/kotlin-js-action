# Kotlin JS GitHub Action SDK

Contains Kotlin JS utilites and wrappers for [actions/toolkit](https://github.com/actions/toolkit) packages, except
for `@actions/github` and `@actions/tool-cache`.  `@actions/tool-cache` will be added once blockind `dukat` bugs are
fixes.

Code is mostly untested, as are the underlying `kotlinx-nodejs` bindings. However, the parts that have been tested imply
the rest "should" work.

In addition to the `@actions` bindings, a utility `Path` class modeled after Python's `pathlib` is included, as are some
miscellaneous utilities (like `JsObject` and an OS enum and detector).
