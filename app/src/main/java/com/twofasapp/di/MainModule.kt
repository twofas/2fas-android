package com.twofasapp.di

import com.twofasapp.android.navigation.Navigator
import com.twofasapp.common.di.KoinModule
import com.twofasapp.ui.main.AppNavigator
import com.twofasapp.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class MainModule : KoinModule {
    override fun provide() = module {
        singleOf(::AppNavigator) { bind<Navigator>() }

        viewModelOf(::MainViewModel)
    }
}