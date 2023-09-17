package com.twofasapp.features.backup

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.databinding.ActivityBackupBinding
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.features.backup.settings.BackupSettingsFragment
import com.twofasapp.features.backup.status.BackupStatusFragment
import com.twofasapp.resources.R
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BackupActivity : BaseActivityPresenter<ActivityBackupBinding>(), BackupContract.View, BackupStatusFragment.Listener,
    BackupSettingsFragment.Listener {

    companion object {
        const val EXTRA_IS_OPENED_FROM_BACKUP_NOTICE = "isOpenedFromBackupNotice"
    }

    private val presenter: BackupContract.Presenter by injectThis()
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            settingsRepository.observeAppSettings().collect {
                makeWindowSecure(allow = it.allowScreenshots)
            }
        }

        setContentView(ActivityBackupBinding::inflate)
        setPresenter(presenter)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, BackupStatusFragment.newInstance(intent.getBooleanExtra(EXTRA_IS_OPENED_FROM_BACKUP_NOTICE, false)))
                .commit()
        }
    }

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun updateToolbar(title: String) {
        viewBinding.toolbar.title = title
    }

    override fun onSettingsClick() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.frame, BackupSettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}