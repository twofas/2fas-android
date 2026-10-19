package com.twofasapp.feature.externalimport.domain

data class GoogleAuthenticatorBatch(
    val size: Int,
    val index: Int,
    val id: Int,
    val servicesCount: Int,
) {
    val isMultiCode: Boolean
        get() = size > 1
}