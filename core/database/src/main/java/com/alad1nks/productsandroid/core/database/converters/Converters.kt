package com.alad1nks.productsandroid.core.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun jsonToStringList(json: String?): List<String>? {
        return json?.let { Json.decodeFromString<List<String>>(it) }
    }

    @TypeConverter
    fun stringListToJson(stringList: List<String>?): String? {
        return stringList?.let { Json.encodeToString(it) }
    }
}
