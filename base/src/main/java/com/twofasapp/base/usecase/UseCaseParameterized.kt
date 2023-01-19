package com.twofasapp.base.usecase

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

interface UseCaseParameterized<in P, out T> {
    fun execute(params: P, subscribeScheduler: Scheduler = io(), observeScheduler: Scheduler = mainThread()): T
}