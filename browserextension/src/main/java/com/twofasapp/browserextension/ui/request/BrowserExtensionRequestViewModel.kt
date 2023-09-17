package com.twofasapp.browserextension.ui.request

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.browserextension.notification.DomainMatcher
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.services.domain.AssignServiceDomainCase
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BrowserExtensionRequestViewModel(
    private val dispatchers: Dispatchers,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val getServicesCase: GetServicesCase,
    private val assignServiceDomainCase: AssignServiceDomainCase,
    private val browserExtRepository: BrowserExtRepository,
) : BaseViewModel() {

    val uiState = MutableStateFlow(BrowserExtensionRequestUiState())

    fun init(
        extensionId: String,
        domain: String?,
    ) {

        viewModelScope.launch(dispatchers.io) {
            val browsers = observePairedBrowsersCase().first()
            val services = getServicesCase()
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
        viewModelScope.launch(dispatchers.io) {
            if (uiState.value.saveMyChoice) {
                assignServiceDomainCase(service, domain)
            }
            browserExtRepository.deleteTokenRequest(requestId)
            onFinish.invoke()
        }
    }

    fun updateSaveMyChoice(checked: Boolean) {
        uiState.update { it.copy(saveMyChoice = checked) }
    }
}