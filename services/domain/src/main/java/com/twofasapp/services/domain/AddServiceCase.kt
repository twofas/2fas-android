package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service

interface AddServiceCase {
    suspend operator fun invoke(service: Service, trigger: Trigger = Trigger.Default): Long

    enum class Trigger { Default, FromBackup }
}