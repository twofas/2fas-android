package com.twofasapp.backup

import com.twofasapp.backup.domain.ExportBackupSuspended
import com.twofasapp.backup.ui.export.ExportBackupViewModel
import com.twofasapp.di.KoinModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class LegacyBackupModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::ExportBackupViewModel)

        singleOf(::ExportBackupSuspended)
    }
}