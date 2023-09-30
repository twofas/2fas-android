package com.twofasapp.storage

import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.twofasapp.data.browserext.local.PairedBrowserDao
import com.twofasapp.data.browserext.local.model.PairedBrowserEntity
import com.twofasapp.data.notifications.local.NotificationsDao
import com.twofasapp.data.notifications.local.model.NotificationEntity
import com.twofasapp.data.services.local.ServiceDao
import com.twofasapp.data.services.local.model.ServiceEntity
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.storage.converter.Converters
import java.text.Normalizer

@Database(
    entities = [
        ServiceEntity::class,
        PairedBrowserEntity::class,
        NotificationEntity::class,
    ],
    version = AppDatabase.DB_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_VERSION = 12
    }

    abstract fun serviceDao(): ServiceDao
    abstract fun pairedBrowserDao(): PairedBrowserDao
    abstract fun notificationDao(): NotificationsDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN badgeColor TEXT")
        database.execSQL("ALTER TABLE local_services ADD COLUMN selectedIcon TEXT")
        database.execSQL("ALTER TABLE local_services ADD COLUMN brandId TEXT")
        database.execSQL("ALTER TABLE local_services ADD COLUMN labelText TEXT")
        database.execSQL("ALTER TABLE local_services ADD COLUMN labelBackgroundColor TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN otpDigits INTEGER")
        database.execSQL("ALTER TABLE local_services ADD COLUMN otpPeriod INTEGER")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN groupId TEXT")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN otpAlgorithm TEXT")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN isDeleted INTEGER")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN authType TEXT")
        database.execSQL("ALTER TABLE local_services ADD COLUMN hotpCounter INTEGER")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        with(database) {
            execSQL("CREATE TABLE local_services_bak AS SELECT * FROM local_services")

            execSQL(
                "CREATE TABLE local_services_backup (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "secret TEXT NOT NULL, " +
                        "serviceTypeId TEXT, " +
                        "iconCollectionId TEXT, " +
                        "source TEXT, " +
                        "otpLink TEXT, " +
                        "otpLabel TEXT, " +
                        "otpAccount TEXT, " +
                        "otpIssuer TEXT, " +
                        "otpDigits INTEGER, " +
                        "otpPeriod INTEGER, " +
                        "otpAlgorithm TEXT, " +
                        "backupSyncStatus TEXT NOT NULL, " +
                        "updatedAt INTEGER NOT NULL, " +
                        "badgeColor TEXT, " +
                        "selectedImageType TEXT, " +
                        "labelText TEXT, " +
                        "labelBackgroundColor TEXT, " +
                        "groupId TEXT, " +
                        "isDeleted INTEGER, " +
                        "authType TEXT, " +
                        "hotpCounter INTEGER, " +
                        "assignedDomains TEXT" +
                        ")"
            )

            val c =
                query("SELECT id, name, secret, type, otpLabel, otpAccount, otpIssuer, otpDigits, otpPeriod, otpAlgorithm, backupSyncStatus, updatedAt, badgeColor, selectedIcon, brandId, labelText, labelBackgroundColor, groupId, isDeleted, authType, hotpCounter, assignedDomains FROM local_services")

            while (c.moveToNext()) {
                val id = c.long("id")
                val name = c.string("name")
                val secret = c.string("secret")
                val type = c.string("type")

                val serviceTypeId = LegacyTypeToId.serviceIds.getOrDefault(type.lowercase(), null)
                var iconCollectionId = ServiceIcons.getIconCollection(serviceTypeId.orEmpty())

                val source = if (type == "ManuallyAdded") "Manual" else "Link"
                val otpLink = null
                val otpLabel = c.stringOrNull("otpLabel")
                val otpAccount = c.stringOrNull("otpAccount")
                val otpIssuer = c.stringOrNull("otpIssuer")
                val otpDigits = c.intOrNull("otpDigits")
                val otpPeriod = c.intOrNull("otpPeriod")
                val otpAlgorithm = c.stringOrNull("otpAlgorithm")
                val backupSyncStatus = c.string("backupSyncStatus")
                val updatedAt = c.long("updatedAt")
                val badgeColor = c.stringOrNull("badgeColor")

                val selectedIcon = c.stringOrNull("selectedIcon")
                val selectedImageType = if (selectedIcon == "Brand") "IconCollection" else selectedIcon

                val brandId = c.stringOrNull("brandId")
                if (brandId.isNullOrBlank().not()) {
                    val serviceTypeIdForBrand = LegacyTypeToId.serviceIds.getOrDefault(brandId!!.lowercase(), null)
                    if (serviceTypeIdForBrand.isNullOrBlank().not()) {
                        iconCollectionId = ServiceIcons.getIconCollection(serviceTypeIdForBrand!!)
                    }
                }

                if (iconCollectionId.isBlank() || iconCollectionId == ServiceIcons.defaultCollectionId) {
                    val serviceTypeIdForManualName = LegacyTypeToId.manualNames.getOrDefault(name.lowercase(), null)
                    if (serviceTypeIdForManualName.isNullOrBlank().not()) {
                        iconCollectionId = ServiceIcons.getIconCollection(serviceTypeIdForManualName!!)
                    }
                }

                val labelText = c.stringOrNull("labelText")
                val labelBackgroundColor = c.stringOrNull("labelBackgroundColor")

                val groupId = c.stringOrNull("groupId")
                val isDeleted = if (c.intOrNull("isDeleted") == 1) 1 else null
                val authType = c.stringOrNull("authType")
                val hotpCounter = c.intOrNull("hotpCounter")
                val assignedDomains = c.stringOrNull("assignedDomains")

                try {
                    execSQL(
                        "INSERT INTO local_services_backup VALUES (" +
                                "$id," +
                                "${name.escape()}," +
                                "${secret.escape()}," +
                                "${serviceTypeId.escape()}," +
                                "${iconCollectionId.escape()}," +
                                "${source.escape()}," +
                                "${otpLink.escape()}," +
                                "${otpLabel.escape()}," +
                                "${otpAccount.escape()}," +
                                "${otpIssuer.escape()}," +
                                "$otpDigits," +
                                "$otpPeriod," +
                                "${otpAlgorithm.escape()}," +
                                "${backupSyncStatus.escape()}," +
                                "$updatedAt," +
                                "${badgeColor.escape()}," +
                                "${selectedImageType.escape()}," +
                                "${labelText.escape()}," +
                                "${labelBackgroundColor.escape()}," +
                                "${groupId.escape()}," +
                                "${isDeleted ?: "NULL"}," +
                                "${authType.escape()}," +
                                "$hotpCounter," +
                                "${assignedDomains.escape()}" +
                                ")"
                    )
                } catch (e: Exception) {
                    execSQL(
                        "INSERT INTO local_services_backup VALUES (" +
                                "$id," +
                                "${name.escapeNormalize()}," +
                                "${secret.escape()}," +
                                "${serviceTypeId.escape()}," +
                                "${iconCollectionId.escape()}," +
                                "${source.escape()}," +
                                "${otpLink.escapeNormalize()}," +
                                "${otpLabel.escapeNormalize()}," +
                                "${otpAccount.escapeNormalize()}," +
                                "${otpIssuer.escapeNormalize()}," +
                                "$otpDigits," +
                                "$otpPeriod," +
                                "${otpAlgorithm.escape()}," +
                                "${backupSyncStatus.escape()}," +
                                "$updatedAt," +
                                "${badgeColor.escape()}," +
                                "${selectedImageType.escape()}," +
                                "${labelText.escape()}," +
                                "${labelBackgroundColor.escape()}," +
                                "${groupId.escape()}," +
                                "${isDeleted ?: "NULL"}," +
                                "${authType.escape()}," +
                                "$hotpCounter," +
                                "${assignedDomains.escapeNormalize()}" +
                                ")"
                    )
                }
            }

            execSQL("DROP TABLE local_services")
            execSQL("ALTER TABLE local_services_backup RENAME to local_services")
        }
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN hotpCounterTimestamp INTEGER")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE local_services ADD COLUMN revealTimestamp INTEGER")
    }
}

private fun Cursor.long(column: String): Long {
    return getLong(columnNames.indexOf(column))
}

private fun Cursor.intOrNull(column: String): Int? {
    return getIntOrNull(columnNames.indexOf(column))
}

private fun Cursor.string(column: String): String {
    return getString(columnNames.indexOf(column))
}

private fun Cursor.stringOrNull(column: String): String? {
    return getStringOrNull(columnNames.indexOf(column))
}

private fun String?.escape(): String {
    return if (this == null) {
        "NULL"
    } else {
        // Remove quotes before insert
        "\"${replace("\"", "")}\""
    }
}

private fun String?.escapeNormalize(): String {
    return if (this == null) {
        "NULL"
    } else {
        // Remove quotes before insert
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD);
        "\"${normalized.replace("\"", "").replace("[^\\x00-\\x7F]", "")}\""
    }
}
