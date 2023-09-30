package com.twofasapp.cipher.backup

import com.twofasapp.common.ktx.encodeBase64ToString

data class DataEncrypted(
    val value: String,
) {
    constructor(
        data: ByteArray,
        salt: ByteArray,
        iv: ByteArray
    ) : this("${data.encodeBase64ToString()}:${salt.encodeBase64ToString()}:${iv.encodeBase64ToString()}")

    private val split = value.split(":")

    val dataEncoded: String = part(0)
    val saltEncoded: String = part(1)
    val ivEncoded: String = part(2)

    private fun part(partIndex: Int): String {
        return try {
            split[partIndex]
        } catch (e: Exception) {
            ""
        }
    }
}
