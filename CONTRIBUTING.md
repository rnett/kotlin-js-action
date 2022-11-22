# Contributing

PRs are welcome!  Issues are also welcome.

If you're making an issue, linking to your action and a failing run will vastly speed up support.

I'll make a decent effort to keep things up to date, but it's not a priority for me.

## Contributing tips

### IDE experience

The IDE experience is not great at the moment.
IntelliJ and Kotlin's support of Gradle composite builds is somewhat lacking, which results in the configuration import for both
`kotlin-js-action` and `test-action` running at the same time and conflicting.
To avoid this, import them one at a time from the Gradle tool window.

Source set settings (`explicitApi` mode, module-wide `@OptIn`s) sometimes aren't picked up.
The Gradle tasks are your source of truth, not necessary the IDE.

### Testing

While it never hurts to test locally, the GitHub actions CI that runs for each pull request is good enough to rely on.
The e2e tests are only possible there.
If you need permissions to run it, feel free to @ me.

If you want to run the tests locally, run `test` in `kotlin-js-action` (make sure it's in `kotlin-js-action`, IntelliJ's Gradle command runner
will often use `test-action`, which will run no tests).

### Project structure

```
kotlin-js-action/               A parent for the library elements.  A Gradle root project.
  kotlin-js-action/             The core JS library.
  serialization/                Extensions for kotlin-js-action that use Kotlinx.serialization.
  kotlin-js-action-plugin/      A Gradle plugin to make configuring your Kotlin/JS build to produce a GitHub action executable easy.
  
test-action/                    A GitHub action that uses this library, for e2e testing.  A Gradle root project.
```

`test-action` consumes `kotlin-js-action` (the parent) as an included build.

The core of this library is `kotlin-js-action`.
It is primarily wrappers around GitHub's `@actions/toolkit` JS packages.

There are also some utilities for working with NodeJS types using idiomatic Kotlin (focused around `Path`s and streams for now).
Additional utilities are always welcome, and a good way to get started.

### Updating `@actions/toolkit` libraries

The process for updating `@actions/toolkit` is surprisingly painless, mostly because they are fairly stable.

1. In `./kotlin-js-action`, run `./gradlew help`. You will see output like:

```
Using old version of npm library @actions/core: Using 1.6.0, but latest was 1.10.0
Using old version of npm library @actions/exec: Using 1.1.0, but latest was 1.1.1
Using old version of npm library @actions/glob: Using 0.2.0, but latest was 0.3.0
Using old version of npm library @actions/io: Using 1.1.1, but latest was 1.1.2
Using old version of npm library @actions/tool-cache: Using 1.7.1, but latest was 2.0.1
Using old version of npm library @actions/github: Using 5.0.0, but latest was 5.1.1
Using old version of npm library @actions/artifact: Using 0.6.1, but latest was 1.1.0
Using old version of npm library @actions/cache: Using 1.0.8, but latest was 3.0.6
Using old version of npm library @actions/http-client: Using 1.0.11, but latest was 2.0.1
```

2. Go to the [`@actions/toolkit`](https://github.com/actions/toolkit) page for each of the out of date library.
   Check the changelog (the `RELEASES.md` for each library, some are in reverse order).
3. For each library that looks like it has breaking changes, update the version in `kotlin-js-action/kotlin-js-action/build.gradle.kts`,
   comment out the `implementation(latestNpm("@actions/tool-cache", "1.7.1", false))` line with the `//TODO breaks dukat` comment, and run
   `generateExternals` (in `kotlin-js-action`).
4. This will generate a bunch of external JS definitions in `kotlin-js-action/kotlin-js-action/externals`.
   Look for any with your newly updated library's name in the filename.
   Either add them to the appropriate package in `kotlin-js-action/kotlin-js-action/src/main/kotlin/internal` (if they are new)
   or diff them with the existing definitions and update as necessary.
   You will need to add `internal` to all of the `external` definitions.
5. Then update the nice wrapper definitions in `com.rnett.action`.
