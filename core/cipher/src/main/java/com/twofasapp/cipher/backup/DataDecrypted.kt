package com.twofasapp.cipher.backup

data class DataDecrypted(
    val data: String,
    val saltEncoded: String,
    val keyEncoded: String,
)