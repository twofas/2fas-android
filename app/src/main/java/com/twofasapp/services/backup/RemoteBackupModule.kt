package com.twofasapp.services.backup

import com.twofasapp.services.backup.usecases.CheckRemoteBackupPassword
import com.twofasapp.services.backup.usecases.GetRemoteBackup
import com.twofasapp.services.backup.usecases.UpdateRemoteBackup
import org.koin.dsl.module

val remoteBackupModule = module {
    single { GetRemoteBackup(get(), get(), get(), get(), get()) }
    single { UpdateRemoteBackup(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single { CheckRemoteBackupPassword(get(), get(), get()) }
}