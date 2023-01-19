package com.twofasapp.features.backup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.twofasapp.resources.R
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.databinding.ActivityBackupBinding
import com.twofasapp.features.backup.settings.BackupSettingsFragment
import com.twofasapp.features.backup.status.BackupStatusFragment

class BackupActivity : BaseActivityPresenter<ActivityBackupBinding>(), BackupContract.View, BackupStatusFragment.Listener, BackupSettingsFragment.Listener {

    companion object {
        const val EXTRA_IS_OPENED_FROM_BACKUP_NOTICE = "isOpenedFromBackupNotice"
    }

    private val presenter: BackupContract.Presenter by injectThis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityBackupBinding::inflate)
        setPresenter(presenter)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, BackupStatusFragment.newInstance(intent.getBooleanExtra(EXTRA_IS_OPENED_FROM_BACKUP_NOTICE, false)))
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
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