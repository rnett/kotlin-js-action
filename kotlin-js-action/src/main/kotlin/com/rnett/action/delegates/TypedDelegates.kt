package com.rnett.action.delegates

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

private fun String.toBooleanHelper() = lowercase().toBooleanStrict()

public fun <D> ReadOnlyProperty<D, String>.isTrue(): ReadOnlyProperty<D, Boolean> = map(String::toBoolean)
public fun <D> ReadWriteProperty<D, String>.isTrue(): ReadWriteProperty<D, Boolean> =
    map(String::toBoolean, Boolean::toString)

public fun <D> ReadOnlyProperty<D, String?>.isTrue(): ReadOnlyProperty<D, Boolean?> = mapNonNull(String::toBoolean)
public fun <D> ReadWriteProperty<D, String?>.isTrue(): ReadWriteProperty<D, Boolean?> =
    mapNonNull(String::toBoolean, Boolean::toString)

public fun <D> ReadOnlyProperty<D, String>.toBoolean(): ReadOnlyProperty<D, Boolean> =
    map(String::toBooleanHelper)

public fun <D> ReadWriteProperty<D, String>.toBoolean(): ReadWriteProperty<D, Boolean> =
    map(String::toBooleanHelper, Boolean::toString)

public fun <D> ReadOnlyProperty<D, String?>.toBoolean(): ReadOnlyProperty<D, Boolean?> =
    mapNonNull(String::toBooleanHelper)

public fun <D> ReadWriteProperty<D, String?>.toBoolean(): ReadOnlyProperty<D, Boolean?> =
    mapNonNull(String::toBooleanHelper, Boolean::toString)


public fun <D> ReadOnlyProperty<D, String>.toInt(): ReadOnlyProperty<D, Int> = map(String::toInt)
public fun <D> ReadWriteProperty<D, String>.toInt(): ReadWriteProperty<D, Int> =
    map(String::toInt, Int::toString)

public fun <D> ReadOnlyProperty<D, String?>.toInt(): ReadOnlyProperty<D, Int?> = mapNonNull(String::toInt)
public fun <D> ReadWriteProperty<D, String?>.toInt(): ReadWriteProperty<D, Int?> =
    mapNonNull(String::toInt, Int::toString)


public fun <D> ReadOnlyProperty<D, String>.toLong(): ReadOnlyProperty<D, Long> = map(String::toLong)
public fun <D> ReadWriteProperty<D, String>.toLong(): ReadWriteProperty<D, Long> =
    map(String::toLong, Long::toString)

public fun <D> ReadOnlyProperty<D, String?>.toLong(): ReadOnlyProperty<D, Long?> = mapNonNull(String::toLong)
public fun <D> ReadWriteProperty<D, String?>.toLong(): ReadWriteProperty<D, Long?> =
    mapNonNull(String::toLong, Long::toString)


public fun <D> ReadOnlyProperty<D, String>.toDouble(): ReadOnlyProperty<D, Double> = map(String::toDouble)
public fun <D> ReadWriteProperty<D, String>.toDouble(): ReadWriteProperty<D, Double> =
    map(String::toDouble, Double::toString)

public fun <D> ReadOnlyProperty<D, String?>.toDouble(): ReadOnlyProperty<D, Double?> = mapNonNull(String::toDouble)
public fun <D> ReadWriteProperty<D, String?>.toDouble(): ReadWriteProperty<D, Double?> =
    mapNonNull(String::toDouble, Double::toString)


public fun <D> ReadOnlyProperty<D, String>.toFloat(): ReadOnlyProperty<D, Float> = map(String::toFloat)
public fun <D> ReadWriteProperty<D, String>.toFloat(): ReadWriteProperty<D, Float> =
    map(String::toFloat, Float::toString)

public fun <D> ReadOnlyProperty<D, String?>.toFloat(): ReadOnlyProperty<D, Float?> = mapNonNull(String::toFloat)
public fun <D> ReadWriteProperty<D, String?>.toFloat(): ReadWriteProperty<D, Float?> =
    mapNonNull(String::toFloat, Float::toString)

private fun String.linesHelper() = lineSequence().filter(String::isNotBlank).map(String::trim).toList()

/**
 * Get the lines of the string.  Trims each lines, does not include blank lines.
 */
public fun <D> ReadOnlyProperty<D, String>.lines(): ReadOnlyProperty<D, List<String>> = map(String::linesHelper)

/**
 * Get the lines of the string.  Trims each lines, does not include blank lines.
 *
 * @param writeSeparator the separator to join the lines with, on write
 */
public fun <D> ReadWriteProperty<D, String>.lines(writeSeparator: String = "\n"): ReadWriteProperty<D, List<String>> =
    map(String::linesHelper) { it.joinToString(writeSeparator) }

/**
 * Get the lines of the string.  Trims each lines, does not inclued blank lines.
 */
public fun <D> ReadOnlyProperty<D, String?>.lines(): ReadOnlyProperty<D, List<String>?> =
    mapNonNull(String::linesHelper)

/**
 * Get the lines of the string.  Trims each lines, does not include blank lines.
 *
 * @param writeSeparator the separator to join the lines with, on write
 */
public fun <D> ReadWriteProperty<D, String?>.lines(writeSeparator: String = "\n"): ReadWriteProperty<D, List<String>?> =
    mapNonNull(String::linesHelper) { it.joinToString(writeSeparator) }


public fun <D> ReadOnlyProperty<D, String>.lowercase(): ReadOnlyProperty<D, String> = map(String::lowercase)
public fun <D> ReadWriteProperty<D, String>.lowercase(): ReadWriteProperty<D, String> = mapBoth(String::lowercase)

public fun <D> ReadOnlyProperty<D, String?>.lowercase(): ReadOnlyProperty<D, String?> = mapNonNull(String::lowercase)
public fun <D> ReadWriteProperty<D, String?>.lowercase(): ReadWriteProperty<D, String?> =
    mapBothNonNull(String::lowercase)


public fun <D> ReadOnlyProperty<D, String>.uppercase(): ReadOnlyProperty<D, String> = map(String::uppercase)
public fun <D> ReadWriteProperty<D, String>.uppercase(): ReadWriteProperty<D, String> = mapBoth(String::uppercase)

public fun <D> ReadOnlyProperty<D, String?>.uppercase(): ReadOnlyProperty<D, String?> = mapNonNull(String::uppercase)
public fun <D> ReadWriteProperty<D, String?>.uppercase(): ReadWriteProperty<D, String?> =
    mapBothNonNull(String::uppercase)


public fun <D> ReadOnlyProperty<D, String>.trim(): ReadOnlyProperty<D, String> = map(String::trim)
public fun <D> ReadWriteProperty<D, String>.trim(): ReadWriteProperty<D, String> = mapBoth(String::trim)

public fun <D> ReadOnlyProperty<D, String?>.trim(): ReadOnlyProperty<D, String?> = mapNonNull(String::trim)
public fun <D> ReadWriteProperty<D, String?>.trim(): ReadWriteProperty<D, String?> = mapBothNonNull(String::trim)