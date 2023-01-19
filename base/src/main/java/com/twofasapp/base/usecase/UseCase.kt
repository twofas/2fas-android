package com.twofasapp.base.usecase

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

interface UseCase<out T> {
    fun execute(subscribeScheduler: Scheduler = io(), observeScheduler: Scheduler = mainThread()): T
}