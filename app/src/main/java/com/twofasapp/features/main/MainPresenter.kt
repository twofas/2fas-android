package com.twofasapp.features.main

import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.usecases.rateapp.RateAppCondition
import com.twofasapp.usecases.rateapp.UpdateRateAppStatus
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val view: MainContract.View,
    private val navigator: ScopedNavigator,
    private val getServices: GetServices,
    private val updateRateAppStatus: UpdateRateAppStatus,
    private val storeHotpServices: StoreHotpServices,
    private val addService: AddService,
    private val parseOtpAuthLink: ParseOtpAuthLink,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
    private val rateAppCondition: RateAppCondition,
    private val analyticsService: AnalyticsService,
) : MainContract.Presenter() {

    private val watchDisposables = CompositeDisposable()

    override fun onViewAttached() {
        storeHotpServices.clear()
        updateRateAppStatus.incrementCounter()

        when {
            rateAppCondition.execute() -> view.showRateApp()
        }
    }

    override fun startObservingPushes() {
    }

    override fun stopObservingPushes() {
        watchDisposables.clear()
    }

    private fun saveService(incomingData: String) {
        parseOtpAuthLink.execute(ParseOtpAuthLink.Params(incomingData))
            .map { convertOtpLinkToService.execute(it) }
            .flatMapCompletable { addService.execute(AddService.Params(it)) }
            .andThen(getServices.execute())
            .subscribe({ }, {})
            .addToDisposables()
    }

    override fun onReviewSuccess() {
        updateRateAppStatus.markShown()
    }

    override fun onReviewFailed(exception: Exception?) {
        analyticsService.captureException(exception)
    }


}
