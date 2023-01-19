package com.twofasapp.featuretoggle.domain

import com.twofasapp.base.usecase.UseCase
import com.twofasapp.featuretoggle.domain.model.RemoteConfig
import io.reactivex.Flowable

interface ObserveRemoteConfigCase : UseCase<Flowable<RemoteConfig>>