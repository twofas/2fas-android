package com.twofasapp.common.storage.internal

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.twofasapp.common.crypto.KeyStoreKeyMissingException
import com.twofasapp.common.logger.Flog
import com.twofasapp.common.storage.Tag
import java.security.Key
import java.security.KeyStore
import javax.crypto.AEADBadTagException
import javax.crypto.KeyGenerator

internal object DataStoreKeyStore {
    private const val keyStoreProvider = "AndroidKeyStore"
    private const val keyPurposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    private const val keyAlgorithm = KeyProperties.KEY_ALGORITHM_AES
    private const val keyBlockMode = KeyProperties.BLOCK_MODE_GCM
    private const val keyPadding = KeyProperties.ENCRYPTION_PADDING_NONE

    private const val keyAlias = "twofasapp_datastore_key"

    private val keyStore: KeyStore
        get() = KeyStore.getInstance(keyStoreProvider).also { it.load(null) }

    val key: Key
        get() = retrying(attempts = 5, delayMs = 500) { getExistingKey() }

    fun getOrCreateKey(): Key {
        return try {
            // Ask several times before accepting that the key really is absent.
            retrying(attempts = 3, delayMs = 100) { getExistingKey() }
        } catch (_: KeyStoreKeyMissingException) {
            retrying(attempts = 5, delayMs = 500) { createKey() }
        }
    }

    fun <T> withRetry(operation: () -> T): T {
        return retrying(
            attempts = 3,
            delayMs = 200,
            retryIf = { it !is KeyStoreKeyMissingException && it !is AEADBadTagException },
            block = operation,
        )
    }

    private fun getExistingKey(): Key {
        return keyStore.getKey(keyAlias, null) ?: throw KeyStoreKeyMissingException(keyAlias)
    }

    private fun createKey(): Key {
        return KeyGenerator.getInstance(keyAlgorithm, keyStoreProvider).run {
            init(
                KeyGenParameterSpec
                    .Builder(keyAlias, keyPurposes)
                    .setBlockModes(keyBlockMode)
                    .setEncryptionPaddings(keyPadding)
                    .setKeySize(256)
                    .build(),
            )

            generateKey()
        }
    }

    private fun <T> retrying(
        attempts: Int,
        delayMs: Long,
        retryIf: (Exception) -> Boolean = { true },
        block: () -> T,
    ): T {
        repeat(attempts - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (retryIf(e).not()) throw e

                // A missing key is expected on a fresh install, not a failure worth logging.
                if (e !is KeyStoreKeyMissingException) {
                    Flog.tag(Tag).w("Keystore attempt ${attempt + 1}/$attempts failed: $e")
                }

                Thread.sleep(delayMs)
            }
        }

        return block()
    }
}