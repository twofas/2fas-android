package com.twofasapp.backup.domain

data class DecryptResult(
    val data: PlainData,
    val saltEncoded: SaltEncoded,
    val keyEncoded: KeyEncoded,
)