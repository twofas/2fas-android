package com.twofasapp.cipher.backup

import com.twofasapp.cipher.backup.internal.BackupKeyGenerator
import com.twofasapp.cipher.backup.internal.BackupSaltGenerator
import com.twofasapp.cipher.internal.CipherAes
import com.twofasapp.common.ktx.decodeBase64ToByteArray
import com.twofasapp.common.ktx.encodeBase64ToString
import javax.crypto.spec.SecretKeySpec

internal class BackupCipherImpl(
    private val cipher: CipherAes,
    private val saltGenerator: BackupSaltGenerator,
    private val keyGenerator: BackupKeyGenerator,
) : BackupCipher {

    override fun encrypt(
        reference: String,
        services: String,
        password: String?,
        keyEncoded: String?,
        saltEncoded: String?,
    ): BackupEncrypted {

        // Use existing salt or generate new one
        val salt = saltEncoded?.decodeBase64ToByteArray() ?: saltGenerator.generate()

        // Use existing key or generate new one based on provided password
        val key = when {
            password != null -> keyGenerator.generate(password = password, salt = salt)
            keyEncoded != null -> createSecretKey(keyEncoded)
            else -> throw RuntimeException("No encryption method")
        }

        // Encrypt data
        val referenceEncrypted = cipher.encrypt(key, reference.toByteArray())
        val servicesEncrypted = cipher.encrypt(key, services.toByteArray())

        return BackupEncrypted(
            reference = DataEncrypted(referenceEncrypted.data, salt, referenceEncrypted.iv),
            services = DataEncrypted(servicesEncrypted.data, salt, servicesEncrypted.iv),
            keyEncoded = key.encoded.encodeBase64ToString(),
            saltEncoded = salt.encodeBase64ToString(),
        )
    }

    override fun decrypt(
        reference: DataEncrypted,
        services: DataEncrypted,
        password: String?,
        keyEncoded: String?
    ): BackupDecrypted {
        return BackupDecrypted(
            reference = decrypt(reference, password, keyEncoded).data,
            services = decrypt(services, password, keyEncoded).data,
        )
    }

    override fun decrypt(
        dataEncrypted: DataEncrypted,
        password: String?,
        keyEncoded: String?
    ): DataDecrypted {

        // Extract splitted data
        val data = dataEncrypted.dataEncoded.decodeBase64ToByteArray()
        val salt = dataEncrypted.saltEncoded.decodeBase64ToByteArray()
        val iv = dataEncrypted.ivEncoded.decodeBase64ToByteArray()

        // Use existing key or generate new one based on provided password
        val key = when {
            password != null -> keyGenerator.generate(password = password, salt = salt)
            keyEncoded != null -> createSecretKey(keyEncoded)
            else -> throw RuntimeException("No decryption method")
        }

        return DataDecrypted(
            data = cipher.decrypt(key, iv, data).data.decodeToString(),
            saltEncoded = salt.encodeBase64ToString(),
            keyEncoded = key.encoded.encodeBase64ToString()
        )
    }

    private fun createSecretKey(keyEncoded: String): SecretKeySpec {
        return SecretKeySpec(keyEncoded.decodeBase64ToByteArray(), "AES")
    }
}