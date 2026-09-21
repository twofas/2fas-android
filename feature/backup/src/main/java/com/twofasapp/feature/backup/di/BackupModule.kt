package com.twofasapp.feature.backup.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.backup.ui.backup.BackupViewModel
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsViewModel
import com.twofasapp.feature.backup.ui.export.BackupExportViewModel
import com.twofasapp.feature.backup.ui.import.BackupImportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class BackupModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::BackupViewModel)
        viewModelOf(::BackupSettingsViewModel)
        viewModelOf(::BackupExportViewModel)
        viewModel { (importFileUri: String?) ->
            BackupImportViewModel(
                importFileUri = importFileUri,
                backupRepository = get(),
                sessionRepository = get(),
            )
        }
    }
}