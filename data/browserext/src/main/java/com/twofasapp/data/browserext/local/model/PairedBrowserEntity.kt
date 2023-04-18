package com.twofasapp.data.browserext.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paired_browsers")
data class PairedBrowserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val extensionPublicKey: String,
    val pairedAt: Long,
)