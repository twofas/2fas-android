package com.twofasapp.feature.browserext.ui.requestapprove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.activity.ActivityHelper
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import org.koin.android.ext.android.inject

class BrowserExtRequestApproveActivity : ComponentActivity() {

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
                Surface {
                    sendBroadcast(BrowserExtRequestReceiver.createIntent(this, payload))
                    finish()
                }
            }
        }
    }
}
