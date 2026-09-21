package com.twofasapp.feature.security.ui.lock

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.twofasapp.base.AuthTracker
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.makeWindowSecure
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LockActivity : AppCompatActivity() {

    private val authTracker: AuthTracker by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val customizationRepository: CustomizationRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(customizationRepository.getSelectedTheme())

        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            settingsRepository.observeAppSettings().collect {
                makeWindowSecure(allow = it.allowScreenshots)
            }
        }

        setContent {
            AppTheme {
                LockScreen {
                    authTracker.onAuthenticated()
                    finishWithSuccess()
                }
            }
        }
    }

    private fun finishWithSuccess() {
        authTracker.onAuthenticated()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val canGoBack = intent.getBooleanExtra("canGoBack", false)

        if (canGoBack.not()) {
            authTracker.onAuthenticateScreen()
        }
    }

    override fun onBackPressed() {
        val canGoBack = intent.getBooleanExtra("canGoBack", false)

        if (canGoBack) {
            finish()
        } else {
            setResult(Activity.RESULT_CANCELED)
            finishAffinity()
        }
    }
}