package com.rnett.action.serialization

import com.rnett.action.delegates.map
import com.rnett.action.delegates.mapNonNull
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlin.properties.ReadOnlyProperty

/**
 * Deserialize the read string.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, String>.deserialize(
    format: StringFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T> = map { format.decodeFromString(serializer, it) }

/**
 * Deserialize the read string if it is non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, String?>.deserializeNotNull(
    format: StringFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromString(serializer, it) }

/**
 * Serialize the read value.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, T>.serialize(
    format: StringFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, String> = map { format.encodeToString(serializer, it) }

/**
 * Serialize the read value, if it is non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: StringFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, String?> = mapNonNull { format.encodeToString(serializer, it) }

/**
 * Deserialize the read string.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, ByteArray>.deserialize(
    format: BinaryFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T> = map { format.decodeFromByteArray(serializer, it) }

/**
 * Deserialize the read string if it is non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, ByteArray?>.deserializeNotNull(
    format: BinaryFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromByteArray(serializer, it) }

/**
 * Serialize the read value.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, T>.serialize(
    format: BinaryFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, ByteArray> = map { format.encodeToByteArray(serializer, it) }

/**
 * Serialize the read value, if it is non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: BinaryFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, ByteArray?> = mapNonNull { format.encodeToByteArray(serializer, it) }

/**
 * Deserialize the read string.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, String>.deserialize(
    format: StringFormat
): ReadOnlyProperty<D, T> = map { format.decodeFromString(it) }

/**
 * Deserialize the read string if it is non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, String?>.deserializeNotNull(
    format: StringFormat
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromString(it) }

/**
 * Serialize the read value.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, T>.serialize(
    format: StringFormat
): ReadOnlyProperty<D, String> = map { format.encodeToString(it) }

/**
 * Serialize the read value, if it is non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: StringFormat
): ReadOnlyProperty<D, String?> = mapNonNull { format.encodeToString(it) }

/**
 * Deserialize the read string.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, ByteArray>.deserialize(
    format: BinaryFormat
): ReadOnlyProperty<D, T> = map { format.decodeFromByteArray(it) }

/**
 * Deserialize the read string if it is non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, ByteArray?>.deserializeNotNull(
    format: BinaryFormat
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromByteArray(it) }

/**
 * Serialize the read value.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, T>.serialize(
    format: BinaryFormat
): ReadOnlyProperty<D, ByteArray> = map { format.encodeToByteArray(it) }

/**
 * Serialize the read value, if it is non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: BinaryFormat
): ReadOnlyProperty<D, ByteArray?> = mapNonNull { format.encodeToByteArray(it) }

