package com.twofasapp.security.ui.biometric

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.twofasapp.android.biometric.BiometricKeyProvider
import com.twofasapp.locale.R
import java.util.concurrent.Executor
import javax.crypto.Cipher

class BiometricDialog(
    private val activity: FragmentActivity,
    private val fragment: Fragment? = null,
    private val titleRes: Int = R.string.biometric_dialog_auth_title,
    private val subtitleRes: Int = R.string.biometric_dialog_auth_subtitle,
    private val cancelRes: Int = R.string.biometric_dialog_auth_cancel,
    private val onSuccess: () -> Unit,
    private val onFailed: () -> Unit,
    private val onError: () -> Unit,
    private val onDismiss: () -> Unit = {},
    private val onBiometricInvalidated: () -> Unit = {},
    private val biometricKeyProvider: BiometricKeyProvider,
) {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun show() {
        executor = ContextCompat.getMainExecutor(activity)

        val callback: BiometricPrompt.AuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess.invoke()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed.invoke()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == 13 || errorCode == 10) {
                    // Cancel action
                    onDismiss.invoke()
                    return
                }

                onError.invoke()
                Toast.makeText(activity, "$errString", Toast.LENGTH_SHORT).show()
            }
        }

        biometricPrompt = if (fragment != null) {
            BiometricPrompt(fragment, executor, callback)
        } else {
            BiometricPrompt(activity, executor, callback)
        }

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(titleRes))
            .setSubtitle(activity.getString(subtitleRes))
            .setNegativeButtonText(activity.getString(cancelRes))
            .build()

        try {
            val cipher = Cipher.getInstance(BiometricKeyProvider.TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, biometricKeyProvider.getSecretKey())
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } catch (e: KeyPermanentlyInvalidatedException) {
            onBiometricInvalidated()
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
    }
}