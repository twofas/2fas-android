package com.twofasapp.data.services.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.twofasapp.data.services.local.model.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Query("SELECT * FROM local_services")
    suspend fun select(): List<ServiceEntity>

    @Query("SELECT * FROM local_services")
    fun observe(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM local_services WHERE id=:id")
    suspend fun select(id: Long): ServiceEntity

    @Query("SELECT * FROM local_services WHERE id=:id")
    fun observe(id: Long): Flow<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serviceEntity: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serviceEntities: List<ServiceEntity>): List<Long>

    @Query("DELETE FROM local_services WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM local_services WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)

    @Query("DELETE FROM local_services WHERE secret == :secret")
    suspend fun deleteBySecret(secret: String)

    @Query("UPDATE local_services SET revealTimestamp = :timestamp WHERE id = :id")
    suspend fun updateRevealTimestamp(id: Long, timestamp: Long)

    @Update
    suspend fun update(entity: ServiceEntity)

    @Update
    suspend fun update(vararg entity: ServiceEntity)

    @Update
    suspend fun update(entities: List<ServiceEntity>)

    @Transaction
    suspend fun cleanUpGroups(groupIds: List<String>) {
        val local = select().toMutableList()

        local.removeAll { it.groupId == null }
        local.removeAll { groupIds.contains(it.groupId) }

        update(
            *local
                .map { it.copy(groupId = null) }
                .toTypedArray(),
        )
    }
}