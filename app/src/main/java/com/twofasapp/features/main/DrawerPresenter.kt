package com.twofasapp.features.main

import com.twofasapp.BuildConfig
import com.twofasapp.resources.R
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.features.main.DrawerEntry.Badge.Label
import com.twofasapp.features.main.DrawerEntry.Badge.None
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.model.SyncStatus

class DrawerPresenter(
    private val navigator: ScopedNavigator,
    private val view: MainContract.View,
    private val observeSyncStatus: ObserveSyncStatus,
) : com.twofasapp.base.BasePresenter() {

    private var entries: List<DrawerEntry> = mutableListOf(
        DrawerEntry(R.drawable.ic_drawer_backup, R.string.backup__2fas_backup) {
            navigator.openBackup()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_security, R.string.settings__security) {
            navigator.openSecurityWithAuth()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_settings, R.string.settings__settings) {
            navigator.openSettings()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_external_import, R.string.settings__external_import) {
            navigator.openExternalImport()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_notifications, R.string.commons__notifications) {
            navigator.openNotifications()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_trash, R.string.settings__trash) {
            navigator.openTrash()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_faq, R.string.settings__support, endIconRes = R.drawable.ic_external_link) {
            navigator.openSupport()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_about, R.string.settings__about) {
            navigator.openAbout()
            view.closeDrawer(200)
        },
        DrawerEntry(R.drawable.ic_drawer_donate, R.string.settings__donate_twofas) {
            navigator.requireActivity().openBrowserApp(url = "https://2fas.com/donate/")
            view.closeDrawer(200)
        },
    )
        .apply {
            if (BuildConfig.DEBUG) {
                add(
                    DrawerEntry(R.drawable.ic_drawer_developer, R.string.settings__developer) {
                        navigator.openDeveloperOptions()
                        view.closeDrawer(200)
                    }
                )
            }
        }
        set(value) {
            field = value
            view.setDrawerItems(value)
        }

    override fun onViewAttached() {
        view.setDrawerItems(entries)

        observeSyncStatus.observe()
            .safelySubscribe {
                when (it) {
                    is SyncStatus.Default -> updateBadge(0, None)
                    is SyncStatus.Synced -> updateBadge(0, None)
                    is SyncStatus.Syncing -> updateBadge(0, Label("Syncing..."))
                    is SyncStatus.Error -> {
                        if (it.shouldShowError()) {
                            updateBadge(0, Label("Error!"))
                        } else {
                            updateBadge(0, None)
                        }
                    }
                }
            }
    }

    private fun updateBadge(position: Int, badge: DrawerEntry.Badge) {
        entries = entries.mapIndexed { index, drawerEntry ->
            if (index == position) {
                drawerEntry.copy(badge = badge)
            } else {
                drawerEntry
            }
        }
    }

    fun updateUnreadNotifications(hasUnreadNotifications: Boolean) {
        if (hasUnreadNotifications) {
            updateBadge(4, DrawerEntry.Badge.DotIcon)
        } else {
            updateBadge(4, DrawerEntry.Badge.None)
        }
    }
}