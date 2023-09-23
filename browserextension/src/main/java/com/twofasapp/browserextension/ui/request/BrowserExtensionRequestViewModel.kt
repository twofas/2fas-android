package com.twofasapp.browserextension.ui.request

import com.twofasapp.base.BaseViewModel
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.browserextension.notification.DomainMatcher
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class BrowserExtensionRequestViewModel(
    private val servicesRepository: ServicesRepository,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val browserExtRepository: BrowserExtRepository,
) : BaseViewModel() {

    val uiState = MutableStateFlow(BrowserExtensionRequestUiState())

    fun init(
        extensionId: String,
        domain: String?,
    ) {

        launchScoped {
            val browsers = observePairedBrowsersCase().first()
            val services = servicesRepository.getServices()
            val matchedServices = domain?.let { DomainMatcher.findServicesMatchingDomain(services, domain) } ?: emptyList()
            val suggestedServices =
                matchedServices.plus(DomainMatcher.findServicesSuggestedForDomain(services, domain).minus(matchedServices.toSet()))

            uiState.update { state ->
                state.copy(
                    browserName = browsers.find { it.id == extensionId }?.name.orEmpty(),
                    suggestedServices = suggestedServices,
                    otherServices = services.minus(suggestedServices.toSet()),
                )
            }
        }
    }

    fun assignDomain(
        requestId: String,
        service: Service,
        domain: String,
        onFinish: () -> Unit
    ) {
        launchScoped {
            if (uiState.value.saveMyChoice) {
                servicesRepository.assignDomainToService(service, domain)
            }
            browserExtRepository.deleteTokenRequest(requestId)
            onFinish.invoke()
        }
    }

    fun updateSaveMyChoice(checked: Boolean) {
        uiState.update { it.copy(saveMyChoice = checked) }
    }
}