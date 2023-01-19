package com.twofasapp.usecases.services

import io.reactivex.processors.PublishProcessor

class ServicesRefreshTrigger {

    private val refreshTrigger: PublishProcessor<Unit> = PublishProcessor.create()

    fun observe() = refreshTrigger

    fun trigger() = refreshTrigger.offer(Unit)
}