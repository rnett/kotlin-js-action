package com.rnett.action.delegates

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

public fun <D> ReadOnlyProperty<D, String>.toBoolean(): ReadOnlyProperty<D, Boolean> = map(String::toBoolean)
public fun <D> ReadWriteProperty<D, String>.toBoolean(): ReadOnlyProperty<D, Boolean> =
    map(String::toBoolean, Boolean::toString)

public fun <D> ReadOnlyProperty<D, String>.toBooleanStrictOrNull(): ReadOnlyProperty<D, Boolean?> =
    map(String::toBooleanStrictOrNull)

public fun <D> ReadWriteProperty<D, String>.toBooleanStrictOrNull(): ReadOnlyProperty<D, Boolean?> =
    map(String::toBooleanStrictOrNull) { it?.toString() ?: "" }

public fun <D> ReadOnlyProperty<D, String?>.toBoolean(): ReadOnlyProperty<D, Boolean?> = mapNonNull(String::toBoolean)
public fun <D> ReadWriteProperty<D, String?>.toBoolean(): ReadOnlyProperty<D, Boolean?> =
    mapNonNull(String::toBoolean, Boolean::toString)

public fun <D> ReadOnlyProperty<D, String?>.toBooleanStrictOrNull(): ReadOnlyProperty<D, Boolean?> =
    mapNonNull(String::toBooleanStrictOrNull)

public fun <D> ReadWriteProperty<D, String?>.toBooleanStrictOrNull(): ReadOnlyProperty<D, Boolean?> =
    mapNonNull(String::toBooleanStrictOrNull) { it?.toString() ?: "" }


public fun <D> ReadOnlyProperty<D, String>.toInt(): ReadOnlyProperty<D, Int> = map(String::toInt)
public fun <D> ReadWriteProperty<D, String>.toInt(): ReadOnlyProperty<D, Int> =
    map(String::toInt, Int::toString)

public fun <D> ReadOnlyProperty<D, String?>.toInt(): ReadOnlyProperty<D, Int?> = mapNonNull(String::toInt)
public fun <D> ReadWriteProperty<D, String?>.toInt(): ReadOnlyProperty<D, Int?> =
    mapNonNull(String::toInt, Int::toString)


public fun <D> ReadOnlyProperty<D, String>.toLong(): ReadOnlyProperty<D, Long> = map(String::toLong)
public fun <D> ReadWriteProperty<D, String>.toLong(): ReadOnlyProperty<D, Long> =
    map(String::toLong, Long::toString)

public fun <D> ReadOnlyProperty<D, String?>.toLong(): ReadOnlyProperty<D, Long?> = mapNonNull(String::toLong)
public fun <D> ReadWriteProperty<D, String?>.toLong(): ReadOnlyProperty<D, Long?> =
    mapNonNull(String::toLong, Long::toString)


public fun <D> ReadOnlyProperty<D, String>.toDouble(): ReadOnlyProperty<D, Double> = map(String::toDouble)
public fun <D> ReadWriteProperty<D, String>.toDouble(): ReadOnlyProperty<D, Double> =
    map(String::toDouble, Double::toString)

public fun <D> ReadOnlyProperty<D, String?>.toDouble(): ReadOnlyProperty<D, Double?> = mapNonNull(String::toDouble)
public fun <D> ReadWriteProperty<D, String?>.toDouble(): ReadOnlyProperty<D, Double?> =
    mapNonNull(String::toDouble, Double::toString)


public fun <D> ReadOnlyProperty<D, String>.toFloat(): ReadOnlyProperty<D, Float> = map(String::toFloat)
public fun <D> ReadWriteProperty<D, String>.toFloat(): ReadOnlyProperty<D, Float> =
    map(String::toFloat, Float::toString)

public fun <D> ReadOnlyProperty<D, String?>.toFloat(): ReadOnlyProperty<D, Float?> = mapNonNull(String::toFloat)
public fun <D> ReadWriteProperty<D, String?>.toFloat(): ReadOnlyProperty<D, Float?> =
    mapNonNull(String::toFloat, Float::toString)


public fun <D> ReadOnlyProperty<D, String>.lines(): ReadOnlyProperty<D, List<String>> = map(String::lines)

/**
 * @param writeSeparator the separator to join the lines with, on write
 */
public fun <D> ReadWriteProperty<D, String>.lines(writeSeparator: String = "\n"): ReadOnlyProperty<D, List<String>> =
    map(String::lines) { it.joinToString(writeSeparator) }

public fun <D> ReadOnlyProperty<D, String?>.lines(): ReadOnlyProperty<D, List<String>?> = mapNonNull(String::lines)

/**
 * @param writeSeparator the separator to join the lines with, on write
 */
public fun <D> ReadWriteProperty<D, String?>.lines(writeSeparator: String = "\n"): ReadOnlyProperty<D, List<String>?> =
    mapNonNull(String::lines) { it.joinToString(writeSeparator) }


public fun <D> ReadOnlyProperty<D, String>.lowercase(): ReadOnlyProperty<D, String> = map(String::lowercase)
public fun <D> ReadWriteProperty<D, String>.lowercase(): ReadOnlyProperty<D, String> = mapBoth(String::lowercase)

public fun <D> ReadOnlyProperty<D, String?>.lowercase(): ReadOnlyProperty<D, String?> = mapNonNull(String::lowercase)
public fun <D> ReadWriteProperty<D, String?>.lowercase(): ReadOnlyProperty<D, String?> =
    mapBothNonNull(String::lowercase)


public fun <D> ReadOnlyProperty<D, String>.uppercase(): ReadOnlyProperty<D, String> = map(String::uppercase)
public fun <D> ReadWriteProperty<D, String>.uppercase(): ReadOnlyProperty<D, String> = mapBoth(String::uppercase)

public fun <D> ReadOnlyProperty<D, String?>.uppercase(): ReadOnlyProperty<D, String?> = mapNonNull(String::uppercase)
public fun <D> ReadWriteProperty<D, String?>.uppercase(): ReadOnlyProperty<D, String?> =
    mapBothNonNull(String::uppercase)


public fun <D> ReadOnlyProperty<D, String>.trim(): ReadOnlyProperty<D, String> = map(String::trim)
public fun <D> ReadWriteProperty<D, String>.trim(): ReadOnlyProperty<D, String> = mapBoth(String::trim)

public fun <D> ReadOnlyProperty<D, String?>.trim(): ReadOnlyProperty<D, String?> = mapNonNull(String::trim)
public fun <D> ReadWriteProperty<D, String?>.trim(): ReadOnlyProperty<D, String?> = mapBothNonNull(String::trim)