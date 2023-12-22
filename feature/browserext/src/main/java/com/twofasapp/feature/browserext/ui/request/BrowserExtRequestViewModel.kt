package com.twofasapp.feature.browserext.ui.request

import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.DomainMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BrowserExtRequestViewModel(
    private val browserExtRepository: BrowserExtRepository,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtRequestUiState())
    private val searchQuery = MutableStateFlow("")

    fun init(payload: BrowserExtRequestPayload) {
        launchScoped {
            searchQuery.collect { query ->
                val domain = payload.domain
                val services = servicesRepository.getServices()
                    .filter {
                        if (query.isNotEmpty()) {
                            it.name.contains(query.trim(), ignoreCase = true) ||
                                    it.tags.map { tag -> tag.lowercase() }.contains(query.lowercase())
                        } else {
                            true
                        }
                    }
                val matched = domain?.let { DomainMatcher.findMatchingDomain(services, domain) } ?: emptyList()
                val suggested =
                    domain?.let { DomainMatcher.findSuggestedForDomain(services, domain) }?.minus(matched.toSet()) ?: emptyList()
                val recommended = matched.plus(suggested)

                uiState.update { state ->
                    state.copy(
                        browserName = browserExtRepository.getPairedBrowser(payload.extensionId).name,
                        domain = payload.domain.orEmpty(),
                        suggestedServices = recommended,
                        otherServices = services.minus(recommended.toSet()),
                        payload = payload,
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.update { query }
    }

    fun assignDomain(service: Service) {
        launchScoped {
            if (uiState.value.saveMyChoice) {
                servicesRepository.assignDomainToService(service, uiState.value.domain)
            }
            browserExtRepository.deleteTokenRequest(uiState.value.payload.requestId)
        }
    }

    fun toggleSaveMyChoice() {
        uiState.update { it.copy(saveMyChoice = it.saveMyChoice.not()) }
    }
}