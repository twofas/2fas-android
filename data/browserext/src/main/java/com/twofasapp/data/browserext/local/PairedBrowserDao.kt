package com.twofasapp.data.browserext.local

import androidx.room.*
import com.twofasapp.data.browserext.local.model.PairedBrowserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PairedBrowserDao {
    @Query("SELECT * FROM paired_browsers")
    suspend fun select(): List<PairedBrowserEntity>

    @Query("SELECT * FROM paired_browsers")
    fun observe(): Flow<List<PairedBrowserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg entity: PairedBrowserEntity)

    @Transaction
    suspend fun updateAll(entities: List<PairedBrowserEntity>) {
        if (entities.isEmpty()) {
            deleteAll()
            return
        }

        val local = select()

        deleteAll()

        insertOrUpdate(
            *entities.map { newEntity ->
                newEntity.copy(extensionPublicKey = local.first { newEntity.id == it.id }.extensionPublicKey)
            }.toTypedArray()
        )
    }

    @Query("DELETE FROM paired_browsers")
    fun deleteAll()
}