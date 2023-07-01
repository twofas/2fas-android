package com.twofasapp.services

import com.twofasapp.di.KoinModule
import com.twofasapp.services.data.ServicesLocalData
import com.twofasapp.services.data.ServicesLocalDataImpl
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.data.ServicesRepositoryImpl
import com.twofasapp.services.domain.AddServiceCase
import com.twofasapp.services.domain.AddServiceCaseImpl
import com.twofasapp.services.domain.AssignServiceDomainCase
import com.twofasapp.services.domain.AssignServiceDomainCaseImpl
import com.twofasapp.services.domain.ConvertOtpToServiceCase
import com.twofasapp.services.domain.ConvertOtpToServiceCaseImpl
import com.twofasapp.services.domain.DeleteServiceUseCase
import com.twofasapp.services.domain.DeleteServiceUseCaseImpl
import com.twofasapp.services.domain.EditServiceCase
import com.twofasapp.services.domain.EditServiceCaseImpl
import com.twofasapp.services.domain.EditServiceUseCase
import com.twofasapp.services.domain.EditServiceUseCaseImpl
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.GetServicesCaseImpl
import com.twofasapp.services.domain.ObserveServiceCase
import com.twofasapp.services.domain.ObserveServiceCaseImpl
import com.twofasapp.services.domain.ObserveServicesCase
import com.twofasapp.services.domain.ObserveServicesCaseImpl
import com.twofasapp.services.ui.EditServiceViewModel
import com.twofasapp.services.ui.changebrand.ChangeBrandViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class ServicesModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::EditServiceViewModel)
        viewModelOf(::ChangeBrandViewModel)

        singleOf(::ServicesLocalDataImpl) { bind<ServicesLocalData>() }
        singleOf(::ServicesRepositoryImpl) { bind<ServicesRepository>() }

        singleOf(::EditServiceUseCaseImpl) { bind<EditServiceUseCase>() }
        singleOf(::DeleteServiceUseCaseImpl) { bind<DeleteServiceUseCase>() }

        singleOf(::GetServicesCaseImpl) { bind<GetServicesCase>() }
        singleOf(::ObserveServicesCaseImpl) { bind<ObserveServicesCase>() }
        singleOf(::ObserveServiceCaseImpl) { bind<ObserveServiceCase>() }
        singleOf(::EditServiceCaseImpl) { bind<EditServiceCase>() }
        singleOf(::AssignServiceDomainCaseImpl) { bind<AssignServiceDomainCase>() }
        singleOf(::AddServiceCaseImpl) { bind<AddServiceCase>() }
        singleOf(::ConvertOtpToServiceCaseImpl) { bind<ConvertOtpToServiceCase>() }

    }
}