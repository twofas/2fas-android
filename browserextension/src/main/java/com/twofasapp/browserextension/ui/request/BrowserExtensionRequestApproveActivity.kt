package com.twofasapp.browserextension.ui.request

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.browserextension.notification.BrowserExtensionRequestPayload
import com.twofasapp.browserextension.notification.BrowserExtensionRequestReceiver
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.designsystem.MainAppTheme
import org.koin.android.ext.android.inject

class BrowserExtensionRequestApproveActivity : BaseComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)
        super.onCreate(savedInstanceState)
        val payload = intent.getParcelableExtra<BrowserExtensionRequestPayload>(BrowserExtensionRequestPayload.Key)!!

        setContent {
            MainAppTheme {
                Surface {
                    sendBroadcast(BrowserExtensionRequestReceiver.createIntent(this, payload))
                    finish()
                }
            }
        }
    }
}
