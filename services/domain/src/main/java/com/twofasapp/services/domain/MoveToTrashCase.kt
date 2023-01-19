package com.twofasapp.services.domain

interface MoveToTrashCase {
    suspend operator fun invoke(serviceId: Long, triggerSync: Boolean)
}