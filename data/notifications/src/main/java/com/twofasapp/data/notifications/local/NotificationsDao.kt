package com.twofasapp.data.notifications.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.twofasapp.data.notifications.local.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {
    @Query("SELECT * FROM notifications")
    suspend fun select(): List<NotificationEntity>

    @Query("SELECT * FROM notifications")
    fun observe(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: NotificationEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg entity: NotificationEntity)

    @Transaction
    suspend fun upsert(entities: List<NotificationEntity>) {
        val localEntities = select()

        // workaround for marking old notifications as read
        val thresholdNotification = localEntities
            .firstOrNull { it.id == "98322bc1-07c5-4ab0-bced-d3b73d982755" }

        val merged = entities.map { remote ->
            val matchedEntity = localEntities.find { it.id == remote.id }

            if (thresholdNotification?.isRead == true && remote.publishTime <= 1672527600000) {
                // all notifications from 2022 year
                remote.copy(isRead = true)
            } else {
                remote.copy(isRead = matchedEntity?.isRead ?: false)
            }
        }

        deleteAll()
        insert(*merged.toTypedArray())
    }

    @Query("DELETE FROM notifications WHERE id IN (:ids)")
    fun delete(ids: List<String>)

    @Query("DELETE FROM notifications WHERE periodicType IS NOT NULL")
    fun deleteAllPeriodic()

    @Query("DELETE FROM notifications WHERE periodicType IS NULL")
    fun deleteAll()
}