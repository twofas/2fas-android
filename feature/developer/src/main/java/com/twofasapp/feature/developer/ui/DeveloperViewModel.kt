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
import com.twofasapp.data.services.otp.ServiceParser
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

    fun generateAllTypes() {
        launchScoped(Dispatchers.IO) {
            servicesRepository.addServices(
                services = buildList {
                    add(ServiceParser.parseService(createLink(issuer = "TOTP Default")))
                    listOf("Google", "Facebook", "Github", "Slack", "Amazon").forEachIndexed { index, issuer ->
                        add(ServiceParser.parseService(createLink(issuer = issuer)).copy(name = "TOTP Default Icon ${index + 1}"))
                    }
                    add(ServiceParser.parseService(createLink(issuer = "HOTP", type = "HOTP", params = mapOf(OtpAuthLink.ParamCounter to "1"))))
                    add(ServiceParser.parseService(createLink(issuer = "Steam", type = "STEAM")))

                    listOf(7, 8).forEach { digits ->
                        add(ServiceParser.parseService(createLink(issuer = "TOTP $digits digits", params = mapOf(OtpAuthLink.ParamDigits to digits.toString()))))
                    }

                    listOf(15, 60).forEach { period ->
                        add(ServiceParser.parseService(createLink(issuer = "TOTP ${period}s period", params = mapOf(OtpAuthLink.ParamPeriod to period.toString()))))
                    }

                    listOf("SHA224", "SHA256", "SHA384", "SHA512").forEach { algorithm ->
                        add(ServiceParser.parseService(createLink(issuer = "TOTP $algorithm", params = mapOf(OtpAuthLink.ParamAlgorithm to algorithm))))
                    }
                },
            )
        }
    }

    private fun createLink(
        issuer: String,
        type: String = "TOTP",
        params: Map<String, String> = emptyMap(),
    ): OtpAuthLink {
        return OtpAuthLink(
            type = type,
            label = "user@test.com",
            secret = randomSecret(),
            issuer = issuer,
            params = params,
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