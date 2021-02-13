package internal



internal typealias HookMethod<O, R> = (options: O) -> dynamic

internal typealias BeforeHook<O> = (options: O) -> Unit

internal typealias ErrorHook<O, E> = (error: E, options: O) -> Unit

internal typealias AfterHook<O, R> = (result: R, options: O) -> Unit

internal typealias WrapHook<O, R> = (hookMethod: HookMethod<O, R>, options: O) -> dynamic