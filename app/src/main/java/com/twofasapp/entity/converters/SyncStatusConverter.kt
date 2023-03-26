package com.twofasapp.entity.converters

import com.twofasapp.entity.SyncStatus
import io.objectbox.converter.PropertyConverter
import java.util.Locale

class SyncStatusConverter : PropertyConverter<SyncStatus, String> {
    override fun convertToEntityProperty(databaseValue: String): SyncStatus {
        try {
            return SyncStatus.valueOf(databaseValue.toUpperCase(Locale.ROOT))
        } catch (e: IllegalArgumentException) {
            return SyncStatus.SYNCED
        }
    }

    override fun convertToDatabaseValue(entityProperty: SyncStatus) =
        entityProperty.name.toLowerCase(Locale.ROOT)
}