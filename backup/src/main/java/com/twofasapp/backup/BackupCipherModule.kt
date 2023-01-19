package com.twofasapp.services.backupcipher

import com.twofasapp.backup.*
import com.twofasapp.backup.DecryptBackup
import com.twofasapp.backup.EncryptBackup
import org.koin.dsl.module

val backupCipherModule = module {
    single<BackupCipherService> { BackupCipherServiceImpl(get(), get(), get()) }
    single<BackupCipherKeyGenerator> { BackupCipherKeyGeneratorPbkdf() }
    single<BackupCipherSaltGenerator> { BackupCipherSaltGeneratorRandom() }

    single { EncryptBackup(get(), get(), get()) }
    single { DecryptBackup(get(), get(), get()) }
}