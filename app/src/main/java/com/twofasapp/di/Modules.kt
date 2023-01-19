package com.twofasapp.di

import com.twofasapp.common.di.CommonModule
import com.twofasapp.data.session.di.DataSessionModule
import com.twofasapp.feature.home.di.HomeModule
import com.twofasapp.feature.startup.di.StartupModule
import com.twofasapp.storage.di.PreferencesModule
import org.koin.core.module.Module

object Modules {
    private val app = listOf(
        AppModule(),
        CommonModule(),
        PreferencesModule(),
    )

    private val data = listOf(
        DataSessionModule(),
    )

    private val feature = listOf(
        MainModule(),
        StartupModule(),
        HomeModule(),
    )

    fun provide(): List<Module> =
        buildList {
            addAll(app)
            addAll(data)
            addAll(feature)
        }.map { it.provide() }
}