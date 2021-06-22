package com.rnett.action.serialization

import com.rnett.action.map
import com.rnett.action.mapNonNull
import kotlinx.serialization.*
import kotlin.properties.ReadOnlyProperty

@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, String>.deserialize(
    format: StringFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T> = map { format.decodeFromString(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, String?>.deserialize(
    format: StringFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromString(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, T>.serialize(
    format: StringFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, String> = map { format.encodeToString(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: StringFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, String?> = mapNonNull { format.encodeToString(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, ByteArray>.deserialize(
    format: BinaryFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T> = map { format.decodeFromByteArray(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, ByteArray?>.deserialize(
    format: BinaryFormat,
    serializer: DeserializationStrategy<T>
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromByteArray(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T> ReadOnlyProperty<D, T>.serialize(
    format: BinaryFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, ByteArray> = map { format.encodeToByteArray(serializer, it) }

@ExperimentalSerializationApi
public fun <D, T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: BinaryFormat,
    serializer: SerializationStrategy<T>
): ReadOnlyProperty<D, ByteArray?> = mapNonNull { format.encodeToByteArray(serializer, it) }

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, String>.deserialize(
    format: StringFormat
): ReadOnlyProperty<D, T> = map { format.decodeFromString(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, String?>.deserialize(
    format: StringFormat
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromString(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, T>.serialize(
    format: StringFormat
): ReadOnlyProperty<D, String> = map { format.encodeToString(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: StringFormat
): ReadOnlyProperty<D, String?> = mapNonNull { format.encodeToString(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, ByteArray>.deserialize(
    format: BinaryFormat
): ReadOnlyProperty<D, T> = map { format.decodeFromByteArray(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, ByteArray?>.deserialize(
    format: BinaryFormat
): ReadOnlyProperty<D, T?> = mapNonNull { format.decodeFromByteArray(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadOnlyProperty<D, T>.serialize(
    format: BinaryFormat
): ReadOnlyProperty<D, ByteArray> = map { format.encodeToByteArray(it) }

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadOnlyProperty<D, T?>.serializeNonNull(
    format: BinaryFormat
): ReadOnlyProperty<D, ByteArray?> = mapNonNull { format.encodeToByteArray(it) }

