package com.twofasapp.persistence.dao

import androidx.room.*
import com.twofasapp.persistence.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
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

        val merged = entities.map { remote ->
            val matchedEntity = localEntities.find { it.id == remote.id }
            remote.copy(isRead = matchedEntity?.isRead ?: false)
        }

        deleteAll()
        insert(*merged.toTypedArray())
    }

    @Query("DELETE FROM notifications WHERE id IN (:ids)")
    fun delete(ids: List<String>)

    @Query("DELETE FROM notifications")
    fun deleteAll()
}