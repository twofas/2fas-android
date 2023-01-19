package com.twofasapp.usecases.services

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

typealias IsEditEnabled = Boolean

class EditStateObserver {

    var isEditEnabled: IsEditEnabled = false
        private set(value) {
            field = value
        }

    private val processor = PublishProcessor.create<IsEditEnabled>()

    fun observe(): Flowable<IsEditEnabled> = processor.share()

    fun offer(isEditEnabled: IsEditEnabled): Boolean {
        this.isEditEnabled = isEditEnabled
        return processor.offer(isEditEnabled)
    }
}