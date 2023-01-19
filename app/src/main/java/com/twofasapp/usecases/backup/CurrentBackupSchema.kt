package com.twofasapp.usecases.backup

import com.twofasapp.featuretoggle.domain.ObserveRemoteConfigCase
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

class CurrentBackupSchema(
    private val observeRemoteConfigCase: ObserveRemoteConfigCase,
) {

    private var isNoticeDisplayed = false

    fun observe(): Flowable<Long> = observeRemoteConfigCase.execute()
        .map { it.androidBackupVersion }
        .observeOn(AndroidSchedulers.mainThread())

    fun isNoticeDisplayed() = isNoticeDisplayed

    fun markNoticeDisplayed() {
        isNoticeDisplayed = true
    }
}