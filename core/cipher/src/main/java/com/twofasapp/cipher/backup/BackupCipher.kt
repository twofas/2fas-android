package com.twofasapp.cipher.backup

interface BackupCipher {

    fun encrypt(
        reference: String,
        services: String,
        password: String?,
        keyEncoded: String? = null,
        saltEncoded: String? = null,
    ): BackupEncrypted


    fun decrypt(
        reference: DataEncrypted,
        services: DataEncrypted,
        password: String?,
        keyEncoded: String?,
    ): BackupDecrypted

    fun decrypt(
        dataEncrypted: DataEncrypted,
        password: String?,
        keyEncoded: String?,
    ): DataDecrypted
}