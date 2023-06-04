package com.twofasapp.data.services.domain

data class RecentlyAddedService(
    val serviceId: Long,
    val source: Source,
) {
    enum class Source {
        Manually, QrScan, QrGallery
    }
}
