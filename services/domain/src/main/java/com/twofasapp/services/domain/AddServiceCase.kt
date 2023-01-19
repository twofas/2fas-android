package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service

interface AddServiceCase {
    suspend operator fun invoke(service: Service, trigger: Trigger = Trigger.Default)

    enum class Trigger { Default, FromBackup }
}