package com.twofasapp.features.services

import com.twofasapp.entity.Services
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

class ServicesProxy {
    private val servicesProxy: PublishProcessor<Services> = PublishProcessor.create()

    fun publish(services: Services) {
        servicesProxy.offer(services)
    }

    fun observe(): Flowable<Services> = servicesProxy
}