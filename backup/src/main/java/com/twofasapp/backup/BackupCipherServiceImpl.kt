package com.twofasapp.backup

import com.twofasapp.backup.domain.BackupCipherDecryptResult
import com.twofasapp.backup.domain.BackupCipherEncryptResult
import com.twofasapp.backup.domain.BackupCipherEncryptedData
import com.twofasapp.backup.domain.BackupCipherPlainData
import com.twofasapp.backup.domain.DecryptResult
import com.twofasapp.backup.domain.EncryptedData
import com.twofasapp.backup.domain.KeyEncoded
import com.twofasapp.backup.domain.Password
import com.twofasapp.backup.domain.PlainData
import com.twofasapp.backup.domain.SaltEncoded
import com.twofasapp.core.cipher.CipherService
import com.twofasapp.core.encoding.decodeBase64ToByteArray
import com.twofasapp.core.encoding.encodeBase64ToString
import javax.crypto.spec.SecretKeySpec

class BackupCipherServiceImpl(
    private val cipherService: CipherService,
    private val saltGenerator: BackupCipherSaltGenerator,
    private val keyGenerator: BackupCipherKeyGenerator,
) : BackupCipherService {

    override fun encrypt(
        data: BackupCipherPlainData,
        saltEncoded: SaltEncoded?,
        password: Password?,
        keyEncoded: KeyEncoded?
    ): BackupCipherEncryptResult {
        return try {
            val salt = saltEncoded?.value?.decodeBase64ToByteArray() ?: saltGenerator.generate()
            val key = when {
                password != null -> keyGenerator.generate(password = password.value, salt = salt)
                keyEncoded != null -> createSecretKey(keyEncoded)
                else -> throw RuntimeException("No encryption method")
            }
            val referenceEncrypted = cipherService.encrypt(key, data.reference.value.toByteArray())
            val servicesEncrypted = cipherService.encrypt(key, data.services.value.toByteArray())

            BackupCipherEncryptResult.Success(
                data = BackupCipherEncryptedData(
                    reference = EncryptedData.build(referenceEncrypted.data, salt, referenceEncrypted.iv),
                    services = EncryptedData.build(servicesEncrypted.data, salt, servicesEncrypted.iv)
                ),
                keyEncoded = KeyEncoded(key.encoded.encodeBase64ToString()),
                saltEncoded = SaltEncoded(salt.encodeBase64ToString()),
            )
        } catch (e: Exception) {
            BackupCipherEncryptResult.Failure(e)
        }
    }

    override fun decrypt(encryptedData: BackupCipherEncryptedData, password: Password?, keyEncoded: KeyEncoded?): BackupCipherDecryptResult {
        return try {
            val referencePlain = decrypt(encryptedData.reference, password, keyEncoded)
            val servicesPlain = decrypt(encryptedData.services, password, keyEncoded)

            BackupCipherDecryptResult.Success(
                BackupCipherPlainData(
                    reference = referencePlain.data,
                    services = servicesPlain.data,
                )
            )
        } catch (e: Exception) {
            BackupCipherDecryptResult.Failure(e)
        }
    }

    override fun decrypt(encryptedData: EncryptedData, password: Password?, keyEncoded: KeyEncoded?): DecryptResult {
        val data = encryptedData.data.value.decodeBase64ToByteArray()
        val salt = encryptedData.salt.value.decodeBase64ToByteArray()
        val iv = encryptedData.iv.value.decodeBase64ToByteArray()

        val key = when {
            password != null -> keyGenerator.generate(password = password.value, salt = salt)
            keyEncoded != null -> createSecretKey(keyEncoded)
            else -> throw RuntimeException("No decryption method")
        }

        return DecryptResult(
            data = PlainData(cipherService.decrypt(key, iv, data).data.decodeToString()),
            saltEncoded = SaltEncoded(salt.encodeBase64ToString()),
            keyEncoded = KeyEncoded(key.encoded.encodeBase64ToString())
        )
    }

    private fun createSecretKey(keyEncoded: KeyEncoded) = SecretKeySpec(keyEncoded.value.decodeBase64ToByteArray(), "AES")
}