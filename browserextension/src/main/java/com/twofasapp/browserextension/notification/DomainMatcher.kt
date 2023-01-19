package com.twofasapp.browserextension.notification

import android.net.Uri
import com.twofasapp.services.domain.model.Service

object DomainMatcher {

    fun extractDomain(url: String): String {
        val uri = Uri.parse(url)
        return uri.host.orEmpty().removePrefix("www.")
    }

    fun findServicesMatchingDomain(services: List<Service>, domain: String): List<Service> {
        return services.filter { it.assignedDomains.contains(domain) }
    }

    fun findServicesSuggestedForDomain(services: List<Service>, domain: String?): List<Service> {
        return services.filter { service -> domain?.contains(service.name, true) ?: false }
    }
}