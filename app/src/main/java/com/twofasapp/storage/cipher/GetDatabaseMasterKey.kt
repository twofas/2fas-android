package com.twofasapp.storage.cipher

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.stringPrefNullable
import kotlinx.coroutines.runBlocking

class GetDatabaseMasterKey(
    dataStoreOwner: DataStoreOwner,
    private val databaseKeyGenerator: DatabaseKeyGenerator,
) : DataStoreOwner by dataStoreOwner {

    private val databaseMasterKey by stringPrefNullable(
        name = "databaseMasterKey",
        encrypted = true,
    )

    fun execute(): String {
        return runBlocking {
            databaseMasterKey.get() ?: databaseKeyGenerator.generate(32).also { generatedKey ->
                databaseMasterKey.set(generatedKey)
            }
        }
    }
}
