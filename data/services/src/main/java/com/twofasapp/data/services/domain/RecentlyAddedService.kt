package com.twofasapp.data.services.domain

data class RecentlyAddedService(
    val service: Service,
    val source: Source,
) {
    enum class Source {
        Manually, QrScan, QrGallery
    }
}
