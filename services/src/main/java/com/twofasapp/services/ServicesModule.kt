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
import com.twofasapp.services.domain.EditServiceCase
import com.twofasapp.services.domain.EditServiceCaseImpl
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.GetServicesCaseImpl
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


        singleOf(::GetServicesCaseImpl) { bind<GetServicesCase>() }
        singleOf(::EditServiceCaseImpl) { bind<EditServiceCase>() }
        singleOf(::AssignServiceDomainCaseImpl) { bind<AssignServiceDomainCase>() }
        singleOf(::AddServiceCaseImpl) { bind<AddServiceCase>() }
        singleOf(::ConvertOtpToServiceCaseImpl) { bind<ConvertOtpToServiceCase>() }

    }
}