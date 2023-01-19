package com.twofasapp.features.services.addedservice

import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel
import com.twofasapp.features.services.ServicesProxy
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.domain.EditServiceUseCase
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.model.Service
import com.twofasapp.usecases.services.ServicesRefreshTrigger
import java.util.Locale

internal class AddedServicePresenter(
    private val view: AddedServiceContract.View,
    private val navigator: ScopedNavigator,
    private val servicesProxy: ServicesProxy,
    private val editService: EditServiceUseCase,
    private val servicesRefreshTrigger: ServicesRefreshTrigger,
    private val storeHotpServices: StoreHotpServices,
) : AddedServiceContract.Presenter() {

    private lateinit var service: ServiceDto

    override fun onViewAttached() {
        service = view.getServiceExtra()

        view.setTitle(service.name)
        view.setSubtitle(service.otpAccount.orEmpty())
        view.customizeClicks()
            .safelySubscribe {
                navigator.openShowService(service)
                view.closeBottomSheet()
            }

        view.editIconClicks()
            .safelySubscribe {
                navigator.openShowService(service, showEditIcon = true)
                view.closeBottomSheet()
            }

        view.refreshCounterClicks()
            .safelySubscribe {
                storeHotpServices.onRefreshCounter(service)
                editService.execute(
                    service.copy(
                        hotpCounter = (service.hotpCounter ?: Service.DefaultHotpCounter) + 1
                    )
                )
                    .safelySubscribe { servicesRefreshTrigger.trigger() }
            }

        servicesProxy.observe()
            .safelySubscribe {
                view.updateService(matchService(it.groups))
            }
    }

    private fun matchService(groups: List<GroupModel>): ServiceModel {
        return groups
            .flatMap { it.services }
            .first { model ->
                model.service.secret.lowercase(Locale.US) == service.secret.lowercase(
                    Locale.US
                )
            }
            .also { this.service = it.service }
    }
}
