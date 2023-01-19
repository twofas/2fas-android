package com.twofasapp.start.domain

interface MigrateUnknownServicesCase {
    suspend operator fun invoke()
}