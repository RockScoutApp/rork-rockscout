package com.rork.rockscout.data.db

import androidx.room.TypeConverter

/**
 * Room type converter for [List<String>] fields.
 * Uses unit separator (U+0001) as delimiter to avoid conflicts
 * with specimen names or tags that may contain commas or pipes.
 */
class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String =
        if (value.isEmpty()) "" else value.joinToString("\u0001")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split("\u0001")
}
