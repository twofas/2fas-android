package com.twofasapp.feature.security.ui.biometric

import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.ktx.toastLong
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import javax.crypto.Cipher
import javax.crypto.SecretKey

@Composable
fun BiometricDialog(
    title: String,
    subtitle: String,
    negative: String,
    requireKeyValidation: Boolean,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    onBiometricInvalidated: () -> Unit = {},
    biometricKeyProvider: BiometricKeyProvider,
) {
    val context = LocalContext.current
    val activity = LocalContext.currentActivity as? FragmentActivity

    if (activity == null) {
        context.toastLong("Could not find FragmentActivity. Restart the app and try again.")
        onDismiss()
        return
    }

    val executor = ContextCompat.getMainExecutor(activity)

    val promptInfo = PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setNegativeButtonText(negative)
        .setConfirmationRequired(false)
        .build()

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            // Failed eg. due to wrong fingerprint
            onDismiss()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode == 13 || errorCode == 10) {
                // Cancel action
                onDismiss()
                return
            }
            context.toastLong("Error: $errString")
            onDismiss()
        }
    }

    val biometricPrompt = BiometricPrompt(activity, executor, callback)

    LaunchedEffect(Unit) {
        try {
            authenticate(
                biometricPrompt = biometricPrompt,
                promptInfo = promptInfo,
                secretKey = biometricKeyProvider.getSecretKey()
            )
        } catch (e: KeyPermanentlyInvalidatedException) {
            if (requireKeyValidation) {
                onBiometricInvalidated()
            } else {
                biometricKeyProvider.deleteSecretKey()

                authenticate(
                    biometricPrompt = biometricPrompt,
                    promptInfo = promptInfo,
                    secretKey = biometricKeyProvider.getSecretKey()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.toastLong("Error: ${e.message}")
            onDismiss()
        }
    }
}

private fun authenticate(
    biometricPrompt: BiometricPrompt,
    promptInfo: PromptInfo,
    secretKey: SecretKey,
) {
    val cipher = Cipher.getInstance(BiometricKeyProvider.TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
}
