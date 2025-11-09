package com.twofasapp.legacy.objectbox.converters

import io.objectbox.converter.PropertyConverter
import java.util.Locale

class ServiceTypeConverter : PropertyConverter<com.twofasapp.prefs.model.ServiceType, String> {
    override fun convertToEntityProperty(databaseValue: String?): com.twofasapp.prefs.model.ServiceType {
        if (databaseValue == null) {
            return com.twofasapp.prefs.model.ServiceType.Null
        }

        try {
            return com.twofasapp.prefs.model.ServiceType.values()
                .firstOrNull { it.name.lowercase(Locale.ROOT) == databaseValue }
                ?: com.twofasapp.prefs.model.ServiceType.Unknown
        } catch (e: Exception) {
            return com.twofasapp.prefs.model.ServiceType.Unknown
        }
    }

    override fun convertToDatabaseValue(entityProperty: com.twofasapp.prefs.model.ServiceType) =
        entityProperty.name.lowercase(Locale.ROOT)
}