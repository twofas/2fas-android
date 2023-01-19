package com.twofasapp.browserextension.ui.request

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.browserextension.notification.DomainMatcher
import com.twofasapp.services.domain.AssignServiceDomainCase
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BrowserExtensionRequestViewModel(
    private val dispatchers: Dispatchers,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val getServicesCase: GetServicesCase,
    private val assignServiceDomainCase: AssignServiceDomainCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(BrowserExtensionRequestUiState())
    val uiState = _uiState.asStateFlow()

    fun init(
        extensionId: String,
        domain: String?,
    ) {

        viewModelScope.launch(dispatchers.io()) {
            val browsers = observePairedBrowsersCase().first()
            val services = getServicesCase()
            val matchedServices = domain?.let { DomainMatcher.findServicesMatchingDomain(services, domain) } ?: emptyList()
            val suggestedServices =
                matchedServices.plus(DomainMatcher.findServicesSuggestedForDomain(services, domain).minus(matchedServices.toSet()))

            _uiState.update { state ->
                state.copy(
                    browserName = browsers.find { it.id == extensionId }?.name.orEmpty(),
                    suggestedServices = suggestedServices,
                    otherServices = services.minus(suggestedServices.toSet()),
                )
            }
        }
    }

    fun assignDomain(service: Service, domain: String, onFinish: () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            assignServiceDomainCase(service, domain)
            onFinish.invoke()
        }
    }
}