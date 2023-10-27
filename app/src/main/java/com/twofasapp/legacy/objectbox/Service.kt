package com.twofasapp.legacy.objectbox

import com.twofasapp.legacy.objectbox.converters.ServiceTypeConverter
import com.twofasapp.legacy.objectbox.converters.SyncStatusConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

// ObjectBox
@Entity
data class Service(
    @Id
    var id: Long = 0,
    val name: String,
    val label: String?,
    val account: String?,
    val secret: String,
    val issuer: String? = null,
    val digits: Int = 6,
    val period: Int = 30,
    val algorithm: String = "SHA1",
    val mobileSecret: String? = null,
    @Deprecated("No longer used since version 2.2.9.")
    var mobileSecretId: Int? = null,
    @Convert(converter = SyncStatusConverter::class, dbType = String::class)
    var syncStatus: SyncStatus,
    @Convert(converter = ServiceTypeConverter::class, dbType = String::class)
    var type: com.twofasapp.prefs.model.ServiceType
)