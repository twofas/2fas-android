package com.twofasapp.persistence.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun stringListToText(list: List<String>?): String? {
        if (list == null || list.isEmpty()) {
            return null
        }

        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun textToStringList(text: String?): List<String>? {
        if (text == null || text.isBlank()) {
            return null
        }

        return text.split(",")
    }
}