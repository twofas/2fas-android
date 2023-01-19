package com.twofasapp.backup.domain

sealed class BackupCipherEncryptResult {
    data class Success(
        val data: BackupCipherEncryptedData,
        val keyEncoded: KeyEncoded,
        val saltEncoded: SaltEncoded,
    ) : BackupCipherEncryptResult()

    data class Failure(val reason: Exception) : BackupCipherEncryptResult()
}