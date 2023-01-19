package com.twofasapp.permissions

import com.twofasapp.di.KoinModule
import com.twofasapp.permissions.internal.FlowPermission
import com.twofasapp.permissions.internal.PermissionProcessor
import com.twofasapp.permissions.internal.PermissionSharedFlow
import com.twofasapp.permissions.internal.RxPermission
import org.koin.dsl.module

class PermissionsModule : KoinModule {
    override fun provide() = module {
        single { PermissionProcessor() }
        single { PermissionSharedFlow() }
        single { RxPermission(get()) }
        single { FlowPermission(get()) }
    }
}