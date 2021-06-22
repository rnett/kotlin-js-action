package com.rnett.action.delegates

import com.rnett.action.camelToSnakeCase
import com.rnett.action.core.inputs
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public abstract class Delegatable(private val camelToSnake: Boolean = false) {
    public abstract fun getRequired(name: String): String
    public abstract fun getOptional(name: String): String?

    protected fun String.delegateName(): String = if (camelToSnake) camelToSnakeCase() else this

    protected open fun delegate(name: String?): ReadOnlyProperty<Any?, String> = Delegate(name)
    protected open fun optionalDelegate(name: String?): ReadOnlyProperty<Any?, String?> = OptionalDelegate(name)

    private inner class Delegate(private val name: String?) : ReadOnlyProperty<Any?, String> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return getRequired(name ?: property.name.delegateName())
        }
    }

    private inner class OptionalDelegate(private val name: String?) : ReadOnlyProperty<Any?, String?> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return getOptional(name ?: property.name.delegateName())
        }
    }

    /**
     * Get the input passed for [name], or [default] if it was not passed.
     */
    public fun getOrElse(name: String, default: () -> String): String = inputs.getOptional(name) ?: default()
}

public abstract class MutableDelegatable(camelToSnake: Boolean = false) : Delegatable(camelToSnake) {
    public abstract fun set(name: String, value: String)

    protected open fun remove(name: String) {

    }

    override fun delegate(name: String?): ReadWriteProperty<Any?, String> = Delegate(name)
    override fun optionalDelegate(name: String?): ReadWriteProperty<Any?, String?> = OptionalDelegate(name)

    private inner class Delegate(private val name: String?) : ReadWriteProperty<Any?, String> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return getRequired(name ?: property.name.delegateName())
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            set(name ?: property.name.delegateName(), value)
        }
    }

    private inner class OptionalDelegate(private val name: String?) : ReadWriteProperty<Any?, String?> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return getOptional(name ?: property.name.delegateName())
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            val name = name ?: property.name.delegateName()
            if (value != null)
                set(name, value)
            else
                remove(name)
        }
    }

    /**
     * Get [name], or set [default] for [name] and return it.
     */
    public fun getOrPut(name: String, default: () -> String): String =
        getOptional(name) ?: default().also {
            set(name, it)
        }
}

/**
 * Map reads from a delegate
 */
public inline fun <D, T, R> ReadOnlyProperty<D, T>.map(crossinline read: (T) -> R): ReadOnlyProperty<D, R> =
    ReadOnlyProperty { thisRef, prop -> read(this@map.getValue(thisRef, prop)) }

/**
 * Map reads from a delegate if the delegate's value is non-null.
 */
public inline fun <D, T : Any, R> ReadOnlyProperty<D, T?>.mapNonNull(crossinline read: (T) -> R): ReadOnlyProperty<D, R?> =
    ReadOnlyProperty { thisRef, prop -> this@mapNonNull.getValue(thisRef, prop)?.let(read) }

/**
 * Get from the delegate, and return [default] if the delegate's value is null.
 *
 * [default] will be re-executed each time null is read.
 */
public inline fun <D, T : R, R> ReadOnlyProperty<D, T?>.ifNull(crossinline default: () -> R): ReadOnlyProperty<D, R> {
    return ReadOnlyProperty { thisRef, prop ->
        getValue(thisRef, prop) ?: default()
    }
}

/**
 * Map reads from a delegate
 */
public inline fun <D, T> ReadWriteProperty<D, T>.map(
    crossinline read: (T) -> T
): ReadWriteProperty<D, T> {
    return object : ReadWriteProperty<D, T> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: T) {
            this@map.setValue(thisRef, property, value)
        }

        override fun getValue(thisRef: D, property: KProperty<*>): T {
            return read(this@map.getValue(thisRef, property))
        }
    }
}

/**
 * Map reads from and writes to a delegate
 */
public inline fun <D, T> ReadWriteProperty<D, T>.mapBoth(
    crossinline both: (T) -> T
): ReadWriteProperty<D, T> {
    return object : ReadWriteProperty<D, T> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: T) {
            this@mapBoth.setValue(thisRef, property, both(value))
        }

        override fun getValue(thisRef: D, property: KProperty<*>): T {
            return both(this@mapBoth.getValue(thisRef, property))
        }
    }
}

/**
 * Map reads from and writes to a delegate
 */
public inline fun <D, T, R> ReadWriteProperty<D, T>.map(
    crossinline read: (T) -> R,
    crossinline write: (R) -> T
): ReadWriteProperty<D, R> {
    return object : ReadWriteProperty<D, R> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: R) {
            this@map.setValue(thisRef, property, write(value))
        }

        override fun getValue(thisRef: D, property: KProperty<*>): R {
            return read(this@map.getValue(thisRef, property))
        }
    }
}

/**
 * Map reads from a delegate if the value is non-null
 */
public inline fun <D, T : Any> ReadWriteProperty<D, T?>.mapNonNull(
    crossinline read: (T) -> T
): ReadWriteProperty<D, T?> {
    return object : ReadWriteProperty<D, T?> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: T?) {
            this@mapNonNull.setValue(thisRef, property, value)
        }

        override fun getValue(thisRef: D, property: KProperty<*>): T? {
            return this@mapNonNull.getValue(thisRef, property)?.let(read)
        }
    }
}

/**
 * Map reads from and writes to a delegate if the value is non-null
 */
public inline fun <D, T : Any> ReadWriteProperty<D, T?>.mapBothNonNull(
    crossinline both: (T) -> T
): ReadWriteProperty<D, T?> {
    return object : ReadWriteProperty<D, T?> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: T?) {
            this@mapBothNonNull.setValue(thisRef, property, value?.let(both))
        }

        override fun getValue(thisRef: D, property: KProperty<*>): T? {
            return this@mapBothNonNull.getValue(thisRef, property)?.let(both)
        }
    }
}

/**
 * Map reads from and writes to a delegate if the value is non-null
 */
public inline fun <D, T : Any, R> ReadWriteProperty<D, T?>.mapNonNull(
    crossinline read: (T) -> R,
    crossinline write: (R) -> T
): ReadWriteProperty<D, R?> {
    return object : ReadWriteProperty<D, R?> {
        override fun setValue(thisRef: D, property: KProperty<*>, value: R?) {
            this@mapNonNull.setValue(thisRef, property, value?.let(write))
        }

        override fun getValue(thisRef: D, property: KProperty<*>): R? {
            return this@mapNonNull.getValue(thisRef, property)?.let(read)
        }
    }
}

/**
 * Get from the delegate, and return [default] if the delegate's value is null.
 *
 * [default] will be re-executed each time null is read.
 */
public inline fun <D, T> ReadWriteProperty<D, T?>.ifNull(crossinline default: () -> T): ReadWriteProperty<D, T> {
    return object : ReadWriteProperty<D, T> {
        override fun getValue(thisRef: D, property: KProperty<*>): T {
            return this@ifNull.getValue(thisRef, property) ?: default()
        }

        override fun setValue(thisRef: D, property: KProperty<*>, value: T) {
            this@ifNull.setValue(thisRef, property, value)
        }
    }
}

/**
 * Read from the delegate if non-null, otherwise set the delegate to [default] and return it.
 *
 * [default] will be re-executed each time null is read.
 */
public inline fun <D, T> ReadWriteProperty<D, T?>.putIfNull(crossinline default: () -> T): ReadWriteProperty<D, T> {
    return object : ReadWriteProperty<D, T> {
        override fun getValue(thisRef: D, property: KProperty<*>): T {
            val value = this@putIfNull.getValue(thisRef, property)
            return if (value != null)
                value
            else {
                val newValue = default()
                this@putIfNull.setValue(thisRef, property, newValue)
                newValue
            }
        }

        override fun setValue(thisRef: D, property: KProperty<*>, value: T) {
            this@putIfNull.setValue(thisRef, property, value)
        }
    }
}