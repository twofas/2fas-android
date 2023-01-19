package com.twofasapp.persistence.dao

import androidx.room.*
import com.twofasapp.persistence.model.ServiceEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM local_services")
    fun select(): Single<List<ServiceEntity>>

    @Query("SELECT * FROM local_services")
    suspend fun selectAll(): List<ServiceEntity>

    @Query("SELECT * FROM local_services")
    fun selectFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM local_services")
    fun observe(): Flowable<List<ServiceEntity>>

    @Query("SELECT * FROM local_services WHERE id=:serviceId")
    fun observe(serviceId: Long): Flow<ServiceEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(serviceEntity: ServiceEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuspend(serviceEntity: ServiceEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg serviceEntity: ServiceEntity): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSuspend(vararg serviceEntity: ServiceEntity)

    @Query("DELETE FROM local_services WHERE id IN (:ids)")
    fun deleteById(ids: List<Long>): Completable

    @Query("DELETE FROM local_services WHERE id == :id")
    suspend fun deleteById(id: Long)
}