package com.twofasapp.cipher.di

import com.twofasapp.cipher.backup.BackupCipher
import com.twofasapp.cipher.backup.BackupCipherImpl
import com.twofasapp.cipher.backup.internal.BackupKeyGenerator
import com.twofasapp.cipher.backup.internal.BackupKeyGeneratorPbkdf
import com.twofasapp.cipher.backup.internal.BackupSaltGenerator
import com.twofasapp.cipher.backup.internal.BackupSaltGeneratorRandom
import com.twofasapp.cipher.internal.CipherAes
import com.twofasapp.common.di.KoinModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class CipherModule : KoinModule {
    override fun provide(): Module = module {
        singleOf(::CipherAes)

        singleOf(::BackupKeyGeneratorPbkdf) { bind<BackupKeyGenerator>() }
        singleOf(::BackupSaltGeneratorRandom) { bind<BackupSaltGenerator>() }
        singleOf(::BackupCipherImpl) { bind<BackupCipher>() }

    }
}