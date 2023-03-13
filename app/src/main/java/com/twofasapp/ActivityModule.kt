package com.twofasapp

import com.twofasapp.backup.ui.export.ExportBackupActivity
import com.twofasapp.backup.ui.export.ExportBackupContract
import com.twofasapp.backup.ui.export.ExportBackupPresenter
import com.twofasapp.features.addserviceqr.AddServiceQrActivity
import com.twofasapp.features.addserviceqr.AddServiceQrContract
import com.twofasapp.features.addserviceqr.AddServiceQrPresenter
import com.twofasapp.features.backup.BackupActivity
import com.twofasapp.features.backup.BackupContract
import com.twofasapp.features.backup.BackupPresenter
import com.twofasapp.features.backup.import.ImportBackupActivity
import com.twofasapp.features.backup.import.ImportBackupContract
import com.twofasapp.features.backup.import.ImportBackupPresenter
import com.twofasapp.features.main.DrawerPresenter
import com.twofasapp.features.main.MainContract
import com.twofasapp.features.main.MainPresenter
import com.twofasapp.features.main.MainServicesActivity
import com.twofasapp.features.main.ToolbarPresenter
import com.twofasapp.features.navigator.ActivityScopedNavigator
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.widgets.configure.WidgetSettingsActivity
import com.twofasapp.widgets.configure.WidgetSettingsContract
import com.twofasapp.widgets.configure.WidgetSettingsPresenter
import org.koin.androidx.scope.ScopeActivity
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

inline fun <reified T : ScopeActivity> Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    scope<T> {
        scoped<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }
        scopeSet()
    }
}

val activityScopeModule = module {

    factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }

    activityScope<MainServicesActivity> {
        scoped { DrawerPresenter(get(), get(), get()) }
        scoped { ToolbarPresenter(get(), get(), get(), get(), get(), get()) }
        scoped<MainContract.Presenter> {
            MainPresenter(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }

    activityScope<AddServiceQrActivity> {
        scoped<AddServiceQrContract.Presenter> {
            AddServiceQrPresenter(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }
    activityScope<BackupActivity> {
        scopedOf(::BackupPresenter) { bind<BackupContract.Presenter>() }
    }
    activityScope<ExportBackupActivity> {
        scopedOf(::ExportBackupPresenter) { bind<ExportBackupContract.Presenter>() }
    }
    activityScope<ImportBackupActivity> {
        scopedOf(::ImportBackupPresenter) { bind<ImportBackupContract.Presenter>() }
    }
    activityScope<WidgetSettingsActivity> {
        scopedOf(::WidgetSettingsPresenter) { bind<WidgetSettingsContract.Presenter>() }
    }
}