package com.twofasapp.feature.browserext.ui.requestapprove

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.enableThemedEdgeToEdge
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BrowserExtRequestApproveActivity : ComponentActivity(), AuthAware {

    private val customizationRepository: CustomizationRepository by inject()
    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val selectedTheme = customizationRepository.getSelectedTheme()
        applyAppTheme(selectedTheme)
        enableThemedEdgeToEdge(theme = selectedTheme)
        super.onCreate(savedInstanceState)

        val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!

        if (payload.action == BrowserExtRequestPayload.Action.Deny) {
            sendBroadcast(BrowserExtRequestReceiver.createIntent(this, payload))
            finish()
        } else {
            authTracker.onBrowserExtRequest()

            lifecycle.addObserver(
                AuthLifecycle(
                    authTracker = get(),
                    navigator = get { parametersOf(this) },
                    authAware = this as? AuthAware,
                ),
            )
        }
    }

    override fun onAuthenticated() {
        val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!
        sendBroadcast(BrowserExtRequestReceiver.createIntent(this, payload))
        finish()
    }
}