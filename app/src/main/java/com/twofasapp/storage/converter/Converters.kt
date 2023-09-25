package com.twofasapp.storage.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun stringListToText(list: List<String>?): String? {
        if (list.isNullOrEmpty()) {
            return null
        }

        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun textToStringList(text: String?): List<String>? {
        if (text.isNullOrBlank()) {
            return null
        }

        return text.split(",")
    }
}