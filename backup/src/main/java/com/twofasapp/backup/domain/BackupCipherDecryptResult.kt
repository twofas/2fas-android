package com.twofasapp.backup.domain

sealed class BackupCipherDecryptResult {
    data class Success(val data: BackupCipherPlainData) : BackupCipherDecryptResult()
    data class Failure(val reason: Exception) : BackupCipherDecryptResult()
}