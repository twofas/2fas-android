/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui

import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

internal class DeveloperViewModel(
    appBuild: AppBuild,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(
        DeveloperUiState(
            appBuild = appBuild,
        ),
    )

    init {
        launchScoped {
            servicesRepository.observeServices().collect { services ->
                uiState.update { it.copy(servicesCount = services.size) }
            }
        }
    }

    fun generateServices(count: Int, onComplete: () -> Unit = {}) {
        launchScoped(Dispatchers.IO) {
            repeat(count) {
                val id = Random.nextInt(9_999_999)

                servicesRepository.addService(
                    link = OtpAuthLink(
                        type = "TOTP",
                        label = "Dev $id",
                        secret = randomSecret(),
                        issuer = "Dev $id",
                        params = emptyMap(),
                        link = null,
                    ),
                )
            }
        }.invokeOnCompletion { onComplete() }
    }

    fun deleteAllServices(onComplete: () -> Unit = {}) {
        launchScoped(Dispatchers.IO) {
            servicesRepository.getServices().forEach { service ->
                servicesRepository.deleteService(service.id)
            }
        }.invokeOnCompletion { onComplete() }
    }

    fun trashAllServices(onComplete: () -> Unit = {}) {
        launchScoped(Dispatchers.IO) {
            servicesRepository.getServices().forEach { service ->
                servicesRepository.trashService(service.id)
            }
        }.invokeOnCompletion { onComplete() }
    }

    private companion object {
        private const val Base32Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

        fun randomSecret(length: Int = 16): String {
            return (0 until length).joinToString("") { Base32Alphabet[Random.nextInt(Base32Alphabet.length)].toString() }
        }
    }
}