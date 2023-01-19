package com.twofasapp.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val category: String,
    val link: String,
    val message: String,
    val publishTime: Long,
    val push: Boolean,
    val platform: String,
    val isRead: Boolean,
)