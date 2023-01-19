package com.twofasapp.core.cipher

import java.security.Key

interface CipherService {
    fun encrypt(key: Key, data: ByteArray): CipherEncryptData
    fun decrypt(key: Key, iv: ByteArray, data: ByteArray): CipherDecryptData
}