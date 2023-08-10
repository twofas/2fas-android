package com.twofasapp.security.ui.biometric

import androidx.biometric.BiometricPrompt

//val callback: BiometricPrompt.AuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
//    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//        super.onAuthenticationSucceeded(result)
//        onSuccess.invoke()
//
////                when (mode) {
////                    BiometricDialogMode.Encrypt -> {
//////                        val data = result.cryptoObject?.cipher?.doFinal(UUID.randomUUID().toString().toByteArray())
//////                        val iv = result.cryptoObject?.cipher?.iv?.copyOf()
//////                        biometricKeyProvider.saveEncryptionData(
//////                            encryptedData = data?.copyOf() ?: byteArrayOf(),
//////                            iv = iv ?: byteArrayOf()
//////                        )
////                    }
////
////                    BiometricDialogMode.Decrypt -> {
//////                        try {
//////                            result.cryptoObject?.cipher?.doFinal(biometricKeyProvider.getEncryptedData())
////                            onSuccess.invoke()
//////                        } catch (e: Exception) {
//////                            onFailed()
//////                            e.printStackTrace()
//////                        }
////                    }
////                }
//    }

//
//            when (mode) {
//                BiometricDialogMode.Encrypt -> cipher.init(Cipher.ENCRYPT_MODE, biometricKeyProvider.getSecretKey())
//                BiometricDialogMode.Decrypt -> {
//
////                    val ivSpec = GCMParameterSpec(128, biometricKeyProvider.getIv())
////                    cipher.init(Cipher.DECRYPT_MODE, biometricKeyProvider.getSecretKey(), ivSpec)
//
//
//                }
//            }