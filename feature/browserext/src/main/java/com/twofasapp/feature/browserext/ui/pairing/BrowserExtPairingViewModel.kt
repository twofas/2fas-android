package com.twofasapp.feature.browserext.ui.pairing

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.encodeBase64ToString
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.browserext.remote.exception.BrowserAlreadyPairedException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore

internal class BrowserExtPairingViewModel(
    savedStateHandle: SavedStateHandle,
    private val browserExtRepository: BrowserExtRepository,
    private val appBuild: AppBuild,
) : ViewModel() {

    private val extensionId: String = savedStateHandle.getOrThrow(NavArg.ExtensionId.name)
    val uiState = MutableStateFlow(BrowserExtPairingUiState())

    companion object {
        private const val MOBILE_DEVICE_ALIAS = "mobileDeviceRsaKey"
    }

    init {
        launchScoped {
            delay(500)
            runSafely {
                val fcmToken = browserExtRepository.getFcmToken()
                val currentMobileDevice = browserExtRepository.getMobileDevice()


                val registeredMobileDevice = if (currentMobileDevice.id.isNotBlank()) {
                    currentMobileDevice
                } else {
                    browserExtRepository.registerMobileDevice(
                        deviceName = currentMobileDevice.name.ifBlank { appBuild.deviceName },
                        devicePublicKey = createDevicePublicKey(),
                        fcmToken = fcmToken,
                    )
                }

                browserExtRepository.pairBrowser(
                    deviceId = registeredMobileDevice.id,
                    extensionId = extensionId,
                    deviceName = registeredMobileDevice.name,
                    devicePublicKey = registeredMobileDevice.publicKey,
                )
            }
                .onSuccess { updateResult(PairingResult.Success) }
                .onFailure { e ->
                    if (e is BrowserAlreadyPairedException) {
                        updateResult(PairingResult.AlreadyPaired)
                    } else {
                        updateResult(PairingResult.Failure)
                    }
                }
        }
    }

    private fun updateResult(result: PairingResult) {
        uiState.update { state -> state.copy(pairing = false, pairingResult = result) }
    }

    private fun createDevicePublicKey(): String {
        return createRsaKey().public.encoded.encodeBase64ToString()
    }

    private fun createRsaKey(): KeyPair {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyStore.deleteEntry(MOBILE_DEVICE_ALIAS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val generator: KeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder(MOBILE_DEVICE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setDigests(KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setKeySize(2048)

        generator.initialize(builder.build())

        return generator.generateKeyPair()
    }
}