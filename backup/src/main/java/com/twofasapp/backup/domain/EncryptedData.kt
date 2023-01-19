package com.twofasapp.backup.domain

import com.twofasapp.core.encoding.encodeBase64ToString

data class EncryptedData(
    val value: String,
) {
    private val split = value.split(":")

    val data: DataEncoded = DataEncoded(part(0))
    val salt: SaltEncoded = SaltEncoded(part(1))
    val iv: IvEncoded = IvEncoded(part(2))

    private fun part(partIndex: Int): String {
        return try {
            split[partIndex]
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        fun build(data: ByteArray, salt: ByteArray, iv: ByteArray): EncryptedData {
            return EncryptedData("${data.encodeBase64ToString()}:${salt.encodeBase64ToString()}:${iv.encodeBase64ToString()}")
        }
    }
}
