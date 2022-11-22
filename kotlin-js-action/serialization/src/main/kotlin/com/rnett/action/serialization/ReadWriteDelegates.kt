package com.rnett.action.serialization

import com.rnett.action.delegates.map
import com.rnett.action.delegates.mapNonNull
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlin.properties.ReadWriteProperty

/**
 * Deserialize reads from and serialize writes to the delegate.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, String>.deserialize(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T> = map({ format.decodeFromString(serializer, it) }, { format.encodeToString(serializer, it) })

/**
 * Deserialize reads from and serialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, String?>.deserializeNotNull(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T?> =
    mapNonNull({ format.decodeFromString(serializer, it) }, { format.encodeToString(serializer, it) })

/**
 * Serialize reads from and deserialize writes to the delegate.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, T>.serialize(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, String> =
    map({ format.encodeToString(serializer, it) }, { format.decodeFromString(serializer, it) })

/**
 * Serialize reads from and deserialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, T?>.serializeNonNull(
    format: StringFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, String?> =
    mapNonNull({ format.encodeToString(serializer, it) }, { format.decodeFromString(serializer, it) })


/**
 * Deserialize reads from and serialize writes to the delegate.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, ByteArray>.deserialize(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T> =
    map({ format.decodeFromByteArray(serializer, it) }, { format.encodeToByteArray(serializer, it) })

/**
 * Deserialize reads from and serialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, ByteArray?>.deserializeNotNull(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, T?> =
    mapNonNull({ format.decodeFromByteArray(serializer, it) }, { format.encodeToByteArray(serializer, it) })

/**
 * Serialize reads from and deserialize writes to the delegate.
 */
@ExperimentalSerializationApi
public fun <D, T> ReadWriteProperty<D, T>.serialize(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, ByteArray> =
    map({ format.encodeToByteArray(serializer, it) }, { format.decodeFromByteArray(serializer, it) })

/**
 * Serialize reads from and deserialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public fun <D, T : Any> ReadWriteProperty<D, T?>.serializeNonNull(
    format: BinaryFormat,
    serializer: KSerializer<T>
): ReadWriteProperty<D, ByteArray?> =
    mapNonNull({ format.encodeToByteArray(serializer, it) }, { format.decodeFromByteArray(serializer, it) })


/**
 * Deserialize reads from and serialize writes to the delegate.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, String>.deserialize(
    format: StringFormat
): ReadWriteProperty<D, T> = map({ format.decodeFromString(it) }, { format.encodeToString(it) })

/**
 * Deserialize reads from and serialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, String?>.deserializeNotNull(
    format: StringFormat
): ReadWriteProperty<D, T?> = mapNonNull({ format.decodeFromString(it) }, { format.encodeToString(it) })

/**
 * Serialize reads from and deserialize writes to the delegate.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, T>.serialize(
    format: StringFormat
): ReadWriteProperty<D, String> = map({ format.encodeToString(it) }, { format.decodeFromString(it) })

/**
 * Serialize reads from and deserialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, T?>.serializeNonNull(
    format: StringFormat
): ReadWriteProperty<D, String?> = mapNonNull({ format.encodeToString(it) }, { format.decodeFromString(it) })


/**
 * Deserialize reads from and serialize writes to the delegate.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, ByteArray>.deserialize(
    format: BinaryFormat
): ReadWriteProperty<D, T> = map({ format.decodeFromByteArray(it) }, { format.encodeToByteArray(it) })

/**
 * Deserialize reads from and serialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, ByteArray?>.deserializeNotNull(
    format: BinaryFormat
): ReadWriteProperty<D, T?> = mapNonNull({ format.decodeFromByteArray(it) }, { format.encodeToByteArray(it) })

/**
 * Serialize reads from and deserialize writes to the delegate.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T> ReadWriteProperty<D, T>.serialize(
    format: BinaryFormat
): ReadWriteProperty<D, ByteArray> = map({ format.encodeToByteArray(it) }, { format.decodeFromByteArray(it) })

/**
 * Serialize reads from and deserialize writes to the delegate, if the values are non-null.
 */
@ExperimentalSerializationApi
public inline fun <D, reified T : Any> ReadWriteProperty<D, T?>.serializeNonNull(
    format: BinaryFormat
): ReadWriteProperty<D, ByteArray?> = mapNonNull({ format.encodeToByteArray(it) }, { format.decodeFromByteArray(it) })