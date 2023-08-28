package com.twofasapp.extensions

import android.view.View
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit


fun View.clicksThrottled(): Flowable<Unit> =
    clicks().toFlowable(BackpressureStrategy.LATEST)
        .throttleFirst(300, TimeUnit.MILLISECONDS)
        .share()

fun androidx.appcompat.widget.Toolbar.navigationClicksThrottled(): Flowable<Unit> =
    navigationClicks().toFlowable(BackpressureStrategy.LATEST)
        .throttleFirst(300, TimeUnit.MILLISECONDS)
        .share()
