# Module Kotlin JS GitHub Action SDK Serialization support

Adds support for Kotlinx serialization, by adding delegate transformers.

Example:

```kotlin
val json = Json{}
var testState: MyData by state.deserialized(json)
```