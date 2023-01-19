package com.twofasapp.security.ui.lock

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import com.twofasapp.base.AuthTracker
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.resources.R
import org.koin.android.ext.android.inject

class LockActivity : AppCompatActivity() {

    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        if (resources.getBoolean(R.bool.isPortraitOnly)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)
        makeWindowSecure()

        setContent {
            AppThemeLegacy {
                Surface {
                    LockScreen {
                        authTracker.onAuthenticated()
                        finishWithSuccess()
                    }
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