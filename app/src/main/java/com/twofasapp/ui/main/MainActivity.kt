package com.twofasapp.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.enableThemedEdgeToEdge
import com.twofasapp.core.design.ktx.makeWindowSecure
import com.twofasapp.core.design.ktx.toastLong
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), AuthAware {

    companion object {
        private const val UPDATE_REQUEST_CODE = 43513
    }

    private val settingsRepository: SettingsRepository by inject()
    private val customizationRepository: CustomizationRepository by inject()
    private val sessionRepository: SessionRepository by inject()
    private val servicesRepository: ServicesRepository by inject()
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdateListener: InstallStateUpdatedListener by lazy {
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackbarForCompleteUpdate()
            }
        }
    }
    private var recalculateTimeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val selectedTheme = customizationRepository.getSelectedTheme()
        applyAppTheme(selectedTheme)
        enableThemedEdgeToEdge(theme = selectedTheme)

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            customizationRepository.observeSelectedTheme().collect { theme ->
                applyAppTheme(theme)
            }
        }

        lifecycleScope.launch {
            settingsRepository.observeAppSettings().collect {
                makeWindowSecure(allow = it.allowScreenshots)
            }
        }

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            MainScreen()
        }

        attachAuthLifecycleObserver()
        checkAppVersionUpdate()
    }

    override fun onResume() {
        super.onResume()
        servicesRepository.setTickerEnabled(true)

        recalculateTimeJob = lifecycleScope.launch {
            sessionRepository.recalculateTimeDelta()
        }
    }

    override fun onAuthenticated() = Unit

    override fun onPause() {
        super.onPause()
        servicesRepository.setTickerEnabled(false)
        recalculateTimeJob?.cancel()
        recalculateTimeJob = null
    }

    private fun attachAuthLifecycleObserver() {
        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware,
            ),
        )
    }

    private fun showSnackbarForCompleteUpdate() {
        try {
            Snackbar.make(
                window.decorView.rootView,
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE,
            ).apply {
                setAction("RESTART") {
                    appUpdateManager.unregisterListener(appUpdateListener)
                    appUpdateManager.completeUpdate()
                }
                show()
            }
        } catch (e: Exception) {
        }
    }

    private fun checkAppVersionUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showSnackbarForCompleteUpdate()
                }

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) &&
                    appUpdateInfo.clientVersionStalenessDays() == null
                ) {
                    lifecycleScope.launch {
                        if (sessionRepository.showAppUpdate()) {
                            sessionRepository.setAppUpdateDisplayed()
                            appUpdateManager.registerListener(appUpdateListener)
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                this@MainActivity,
                                UPDATE_REQUEST_CODE,
                            )
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        if (requestCode == UPDATE_REQUEST_CODE) {
            toastLong("Updating. Please wait...")
            return
        }
    }
}