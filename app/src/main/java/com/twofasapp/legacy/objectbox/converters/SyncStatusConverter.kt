package com.twofasapp.legacy.objectbox.converters

import com.twofasapp.legacy.objectbox.SyncStatus
import io.objectbox.converter.PropertyConverter
import java.util.Locale

class SyncStatusConverter : PropertyConverter<SyncStatus, String> {
    override fun convertToEntityProperty(databaseValue: String): SyncStatus {
        try {
            return SyncStatus.valueOf(databaseValue.uppercase(Locale.ROOT))
        } catch (e: IllegalArgumentException) {
            return SyncStatus.SYNCED
        }
    }

    override fun convertToDatabaseValue(entityProperty: SyncStatus) =
        entityProperty.name.lowercase(Locale.ROOT)
}