package com.twofasapp.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class JsonSerializer {
    val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        coerceInputValues = true
    }

    inline fun <reified T> serialize(data: T): String = json.encodeToString(data)

    inline fun <reified T> serializePretty(data: T): String = JSONObject(serialize(data)).toString(2)

    inline fun <reified T> deserialize(data: String) = json.decodeFromString<T>(data)
}


