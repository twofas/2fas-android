package com.twofasapp.feature.backup.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.backup.ui.backup.BackupViewModel
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsViewModel
import com.twofasapp.feature.backup.ui.export.BackupExportViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class BackupModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::BackupViewModel)
        viewModelOf(::BackupSettingsViewModel)
        viewModelOf(::BackupExportViewModel)
    }
}