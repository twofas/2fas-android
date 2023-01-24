package com.twofasapp.start.ui.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.twofasapp.base.AuthTracker
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.navigation.StartDirections
import com.twofasapp.navigation.StartRouter
import com.twofasapp.prefs.usecase.AppThemePreference
import com.twofasapp.start.R
import com.twofasapp.start.domain.DeeplinkHandler
import com.twofasapp.start.domain.GetShowOnboardingCase
import com.twofasapp.start.domain.work.OnAppUpdatedWorkDispatcher
import com.twofasapp.time.domain.work.SyncTimeWorkDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class StartActivity : AppCompatActivity() {

    private val appThemePreference: AppThemePreference by inject()
    private val getShowOnboardingCase: GetShowOnboardingCase by inject()
    private val deeplinkHandler: DeeplinkHandler by inject()
    private val authTracker: AuthTracker by inject()
    private val onAppUpdatedWorkDispatcher: OnAppUpdatedWorkDispatcher by inject()
    private val syncTimeWorkDispatcher: SyncTimeWorkDispatcher by inject()
    private val router: StartRouter by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(appThemePreference.get())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        lifecycleScope.launch(Dispatchers.IO) {
            deeplinkHandler.setQueuedDeeplink(incomingData = intent.data?.toString())

            if (getShowOnboardingCase()) {
//                router.navigate(StartDirections.Onboarding)
            } else {
                authTracker.onSplashScreen()
                router.navigate(StartDirections.Main)
            }

            onAppUpdatedWorkDispatcher.dispatch()
            syncTimeWorkDispatcher.dispatch()
        }
    }
}
