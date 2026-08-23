package com.twofasapp.storage.cipher

import android.content.Context
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.stringPrefNullable
import kotlinx.coroutines.runBlocking

class GetDatabaseMasterKey(
    private val context: Context,
    dataStoreOwner: DataStoreOwner,
    private val databaseKeyGenerator: DatabaseKeyGenerator,
) : DataStoreOwner by dataStoreOwner {

    private val databaseMasterKey by stringPrefNullable(
        name = "databaseMasterKey",
        encrypted = true,
    )

    fun execute(): String {
        return runBlocking {
            databaseMasterKey.get() ?: generateAndPersistKey()
        }
    }

    private suspend fun generateAndPersistKey(): String {
        // No key is stored yet. Minting a new one is only safe on a genuine fresh install. If the
        // legacy database already exists, its master key failed to migrate - generating a new key
        // here would permanently lock the user out of the existing (old-key) database. Fail loud and
        // recoverable instead: the migration is retried on the next launch.
        check(context.getDatabasePath(DatabaseName).exists().not()) {
            "Database exists but the master key is missing - aborting to avoid overwriting it."
        }

        return databaseKeyGenerator.generate(32).also { generatedKey ->
            databaseMasterKey.set(generatedKey)
        }
    }

    companion object {
        private const val DatabaseName = "database-2fas"
    }
}