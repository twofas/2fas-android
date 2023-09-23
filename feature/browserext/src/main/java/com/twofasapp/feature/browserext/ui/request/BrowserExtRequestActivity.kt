package com.twofasapp.feature.browserext.ui.request

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.activity.ActivityHelper
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import org.koin.android.ext.android.inject

class BrowserExtRequestActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityHelper.onCreate(
            activity = this,
            selectedTheme = settingsRepository.getAppSettings().selectedTheme,
            allowScreenshots = settingsRepository.getAppSettings().allowScreenshots,
        )
        super.onCreate(savedInstanceState)

        val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!

        setContent {
            MainAppTheme {
                BrowserExtRequestScreen(payload = payload)
            }
        }
    }
}