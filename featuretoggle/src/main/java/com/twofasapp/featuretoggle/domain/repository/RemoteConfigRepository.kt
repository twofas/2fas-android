package com.twofasapp.featuretoggle.domain.repository

import com.twofasapp.featuretoggle.domain.model.RemoteConfig
import io.reactivex.Flowable

internal interface RemoteConfigRepository {
    fun fetchAndActivate()
    fun observe(): Flowable<RemoteConfig>
}