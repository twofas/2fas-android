package com.twofasapp.serialization

import com.twofasapp.di.KoinModule
import org.koin.dsl.module

class SerializationModule : KoinModule {

    override fun provide() = module {
        single { JsonSerializer() }
    }
}