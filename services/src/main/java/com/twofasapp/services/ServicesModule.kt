package com.twofasapp.services

import com.twofasapp.di.KoinModule
import com.twofasapp.services.data.*
import com.twofasapp.services.domain.*
import com.twofasapp.services.ui.ServiceViewModel
import com.twofasapp.services.ui.changebrand.ChangeBrandViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class ServicesModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::ServiceViewModel)
        viewModelOf(::ChangeBrandViewModel)

        singleOf(::ServicesLocalDataImpl) { bind<ServicesLocalData>() }
        singleOf(::ServicesRepositoryImpl) { bind<ServicesRepository>() }
        singleOf(::GroupsRepositoryImpl) { bind<GroupsRepository>() }

        singleOf(::EditServiceUseCaseImpl) { bind<EditServiceUseCase>() }
        singleOf(::DeleteServiceUseCaseImpl) { bind<DeleteServiceUseCase>() }

        singleOf(::GetServicesCaseImpl) { bind<GetServicesCase>() }
        singleOf(::GetGroupsCaseImpl) { bind<GetGroupsCase>() }
        singleOf(::ObserveServicesCaseImpl) { bind<ObserveServicesCase>() }
        singleOf(::ObserveServiceCaseImpl) { bind<ObserveServiceCase>() }
        singleOf(::EditServiceCaseImpl) { bind<EditServiceCase>() }
        singleOf(::AssignServiceDomainCaseImpl) { bind<AssignServiceDomainCase>() }
        singleOf(::MoveToTrashCaseImpl) { bind<MoveToTrashCase>() }
        singleOf(::AddServiceCaseImpl) { bind<AddServiceCase>() }
        singleOf(::ConvertOtpToServiceCaseImpl) { bind<ConvertOtpToServiceCase>() }

    }
}