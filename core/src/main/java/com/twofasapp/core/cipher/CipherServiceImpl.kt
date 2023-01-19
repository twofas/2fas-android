package com.twofasapp.core.cipher

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class CipherServiceImpl : CipherService {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    override fun encrypt(key: Key, data: ByteArray): CipherEncryptData {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)

        return CipherEncryptData(
            data = encrypted,
            iv = iv,
        )
    }

    override fun decrypt(key: Key, iv: ByteArray, data: ByteArray): CipherDecryptData {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        val decrypted = cipher.doFinal(data)

        return CipherDecryptData(data = decrypted)
    }
}