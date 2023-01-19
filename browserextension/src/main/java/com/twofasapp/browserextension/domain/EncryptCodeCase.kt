package com.twofasapp.browserextension.domain

import com.twofasapp.core.encoding.decodeBase64ToByteArray
import com.twofasapp.core.encoding.encodeBase64ToString
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class EncryptCodeCase {

    data class Params(
        val code: String,
        val extensionPublicKey: String,
    )

    suspend operator fun invoke(params: Params): String {
        val publicKeySpec = X509EncodedKeySpec(params.extensionPublicKey.decodeBase64ToByteArray())
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)

        val cipher: Cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
            .apply {
                // To use SHA-256 the main digest and SHA-1 as the MGF1 digest
                init(Cipher.ENCRYPT_MODE, publicKey, OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT))
                // To use SHA-512 for both digests
                // init(Cipher.ENCRYPT_MODE, publicKey, OAEPParameterSpec("SHA-510", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT))
            }
        val bytes = cipher.doFinal(params.code.toByteArray())
        return bytes.encodeBase64ToString()
    }
}