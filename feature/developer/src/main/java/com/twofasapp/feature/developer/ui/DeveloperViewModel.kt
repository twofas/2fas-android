/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui

import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.parsers.SupportedServices
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
        uiState.update { it.copy(supportedServicesCount = SupportedServices.list.size) }

        launchScoped {
            servicesRepository.observeServices().collect { services ->
                uiState.update { it.copy(servicesCount = services.size) }
            }
        }

        launchScoped {
            servicesRepository.observeDeletedServices().collect { services ->
                uiState.update { it.copy(trashedServicesCount = services.size) }
            }
        }
    }

    fun generateRandomServices(count: Int) {
        launchScoped(Dispatchers.IO) {
            servicesRepository.addServicesFromLinks(
                links = List(count) {
                    createLink(issuer = "Service ${Random.nextInt(1_000_000).toString().padStart(6, '0')}")
                },
            )
        }
    }

    fun generateSupportedServices(count: Int?) {
        launchScoped(Dispatchers.IO) {
            val supportedServices = SupportedServices.list.shuffled()

            servicesRepository.addServicesFromLinks(
                links = List(count ?: supportedServices.size) { index ->
                    val supportedService = supportedServices.getOrNull(index % supportedServices.size.coerceAtLeast(1))

                    createLink(
                        issuer = supportedService?.let { it.issuers.firstOrNull() ?: it.name }
                            ?: "Service ${Random.nextInt(1_000_000).toString().padStart(6, '0')}",
                    )
                },
            )
        }
    }

    private fun createLink(issuer: String): OtpAuthLink {
        return OtpAuthLink(
            type = "TOTP",
            label = "user@test.com",
            secret = randomSecret(),
            issuer = issuer,
            params = emptyMap(),
            link = null,
        )
    }

    fun trashServices(count: Int?) {
        launchScoped(Dispatchers.IO) {
            val ids = servicesRepository.getServices().map { it.id }

            servicesRepository.trashServices(
                ids = if (count != null) ids.take(count) else ids,
            )
        }
    }

    fun emptyTrash() {
        launchScoped(Dispatchers.IO) {
            servicesRepository.deleteServices(
                ids = servicesRepository.getServicesIncludingDeleted()
                    .filter { it.isDeleted }
                    .map { it.id },
            )
        }
    }

    private companion object {
        private const val Base32Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

        fun randomSecret(length: Int = 16): String {
            return (0 until length).joinToString("") { Base32Alphabet[Random.nextInt(Base32Alphabet.length)].toString() }
        }
    }
}