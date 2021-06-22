package com.rnett.action.serialization

import com.rnett.action.map
import com.rnett.action.mapNonNull
import kotlinx.serialization.*
import kotlin.properties.ReadWriteProperty


@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, String>.serialized(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T> = map({ format.decodeFromString(serializer, it) }, { format.encodeToString(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, String?>.serialized(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T?> =
    mapNonNull({ format.decodeFromString(serializer, it) }, { format.encodeToString(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, T>.deserialized(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, String> =
    map({ format.encodeToString(serializer, it) }, { format.decodeFromString(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, T?>.deserializedNonNull(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, String?> =
    mapNonNull({ format.encodeToString(serializer, it) }, { format.decodeFromString(serializer, it) })


@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, ByteArray>.serialized(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T> =
    map({ format.decodeFromByteArray(serializer, it) }, { format.encodeToByteArray(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, ByteArray?>.serialized(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T?> =
    mapNonNull({ format.decodeFromByteArray(serializer, it) }, { format.encodeToByteArray(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, T>.deserialized(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, ByteArray> =
    map({ format.encodeToByteArray(serializer, it) }, { format.decodeFromByteArray(serializer, it) })

@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, T?>.deserializedNonNull(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, ByteArray?> =
    mapNonNull({ format.encodeToByteArray(serializer, it) }, { format.decodeFromByteArray(serializer, it) })


@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, String>.serialized(
    format: StringFormat
): ReadWriteProperty<D, T> = map({ format.decodeFromString(it) }, { format.encodeToString(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, String?>.serialized(
    format: StringFormat
): ReadWriteProperty<D, T?> = mapNonNull({ format.decodeFromString(it) }, { format.encodeToString(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, T>.deserialized(
    format: StringFormat
): ReadWriteProperty<D, String> = map({ format.encodeToString(it) }, { format.decodeFromString(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, T?>.deserializedNonNull(
    format: StringFormat
): ReadWriteProperty<D, String?> = mapNonNull({ format.encodeToString(it) }, { format.decodeFromString(it) })


@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, ByteArray>.serialized(
    format: BinaryFormat
): ReadWriteProperty<D, T> = map({ format.decodeFromByteArray(it) }, { format.encodeToByteArray(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, ByteArray?>.serialized(
    format: BinaryFormat
): ReadWriteProperty<D, T?> = mapNonNull({ format.decodeFromByteArray(it) }, { format.encodeToByteArray(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, T>.deserialized(
    format: BinaryFormat
): ReadWriteProperty<D, ByteArray> = map({ format.encodeToByteArray(it) }, { format.decodeFromByteArray(it) })

@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, T?>.deserializedNonNull(
    format: BinaryFormat
): ReadWriteProperty<D, ByteArray?> = mapNonNull({ format.encodeToByteArray(it) }, { format.decodeFromByteArray(it) })