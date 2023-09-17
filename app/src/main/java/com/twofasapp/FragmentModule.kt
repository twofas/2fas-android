package com.twofasapp

import com.twofasapp.features.backup.settings.BackupSettingsContract
import com.twofasapp.features.backup.settings.BackupSettingsFragment
import com.twofasapp.features.backup.settings.BackupSettingsPresenter
import com.twofasapp.features.backup.status.BackupStatusContract
import com.twofasapp.features.backup.status.BackupStatusFragment
import com.twofasapp.features.backup.status.BackupStatusPresenter
import com.twofasapp.features.navigator.ActivityScopedNavigator
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.qrscanner.QrScannerContract
import com.twofasapp.qrscanner.QrScannerPresenter
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

inline fun <reified T : ScopeFragment> Module.fragmentScope(scopeSet: ScopeDSL.() -> Unit) {
    scope<T> {
        scoped<ScopedNavigator> { ActivityScopedNavigator(get<T>().requireActivity(), get()) }
        scopeSet()
    }
}

val fragmentScopeModule = module {

    fragmentScope<BackupStatusFragment> {
        scoped<BackupStatusContract.Presenter> {
            BackupStatusPresenter(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }
    fragmentScope<com.twofasapp.qrscanner.QrScannerFragment> {
        scopedOf(::QrScannerPresenter) { bind<QrScannerContract.Presenter>() }
    }
    fragmentScope<BackupSettingsFragment> {
        scopedOf(::BackupSettingsPresenter) { bind<BackupSettingsContract.Presenter>() }
    }

}