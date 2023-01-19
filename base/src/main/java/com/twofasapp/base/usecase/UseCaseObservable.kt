package com.twofasapp.base.usecase

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

interface UseCaseObservable<T> {
    fun observe(subscribeScheduler: Scheduler = io(), observeScheduler: Scheduler = mainThread()): Flowable<T>
    fun publish(model: T) = Unit
}