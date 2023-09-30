package com.twofasapp.feature.browserext.notification

import android.net.Uri
import com.twofasapp.common.domain.Service

object DomainMatcher {

    fun extractDomain(url: String): String {
        val uri = Uri.parse(url)
        return uri.host.orEmpty().removePrefix("www.")
    }

    fun findMatchingDomain(services: List<Service>, domain: String): List<Service> {
        return services.filter { it.assignedDomains.contains(domain) }
    }

    fun findSuggestedForDomain(services: List<Service>, domain: String?): List<Service> {
        return services.filter { service -> domain?.contains(service.name, true) ?: false }
    }
}