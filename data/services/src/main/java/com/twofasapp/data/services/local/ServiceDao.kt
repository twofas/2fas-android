package com.twofasapp.data.services.local

import androidx.room.*
import com.twofasapp.data.services.local.model.ServiceEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Query("SELECT * FROM local_services")
    suspend fun select(): List<ServiceEntity>

    @Query("SELECT * FROM local_services")
    fun observe(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM local_services WHERE id=:id")
    suspend fun select(id: Long): ServiceEntity

    @Query("SELECT * FROM local_services WHERE secret=:secret")
    suspend fun selectBySecret(secret: String): ServiceEntity?

    @Query("SELECT * FROM local_services WHERE id=:id")
    fun observe(id: Long): Flow<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serviceEntity: ServiceEntity): Long

    @Query("DELETE FROM local_services WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM local_services WHERE secret == :secret")
    suspend fun deleteBySecret(secret: String)

    @Update
    suspend fun update(entity: ServiceEntity)

    @Update
    suspend fun update(vararg entity: ServiceEntity)

    @Transaction
    suspend fun cleanUpGroups(groupIds: List<String>) {
        val local = select().toMutableList()

        local.removeAll { it.groupId == null }
        local.removeAll { groupIds.contains(it.groupId) }

        update(*local
            .map { it.copy(groupId = null) }
            .toTypedArray())
    }

    // Legacy
    @Query("SELECT * FROM local_services")
    fun legacySelect(): Single<List<ServiceEntity>>

    @Query("SELECT * FROM local_services")
    suspend fun legacySelectAll(): List<ServiceEntity>

    @Query("SELECT * FROM local_services")
    fun legacySelectFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM local_services")
    fun legacyObserve(): Flowable<List<ServiceEntity>>

    @Query("SELECT * FROM local_services WHERE id=:serviceId")
    fun legacyObserve(serviceId: Long): Flow<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun legacyInsert(serviceEntity: ServiceEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun legacyInsertSuspend(serviceEntity: ServiceEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun legacyUpdate(vararg serviceEntity: ServiceEntity): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun legacyUpdateSuspend(vararg serviceEntity: ServiceEntity)

    @Query("DELETE FROM local_services WHERE id IN (:ids)")
    fun legacyDeleteById(ids: List<Long>): Completable

    @Query("DELETE FROM local_services WHERE id == :id")
    suspend fun legacyDeleteById(id: Long)
}