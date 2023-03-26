package com.twofasapp.features.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.play.core.review.ReviewManagerFactory
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.databinding.ActivityMainBinding
import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction
import com.twofasapp.design.dialogs.ConfirmDialog
import com.twofasapp.features.addserviceqr.AddServiceQrActivity
import com.twofasapp.features.addserviceqr.ScanInfoDialog
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.resources.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

class MainServicesActivity : BaseActivityPresenter<ActivityMainBinding>(), MainContract.View, AuthAware {

    private val presenter: MainContract.Presenter by injectThis()

    //    private val fetchTokenRequestsCase: FetchTokenRequestsCase by inject()
//    private val observeMobileDeviceCase: ObserveMobileDeviceCase by inject()
    private val authenticationDialogs = mutableMapOf<String, MaterialDialog>()
    private val removeQrReminderDialog: ScanInfoDialog by lazy { ScanInfoDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding::inflate)
        setPresenter(presenter)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch(Dispatchers.IO) {
//                    launch(Dispatchers.IO) {
//
//                        try {
//                            val mobileDevice = observeMobileDeviceCase.invoke().first()
//                            val tokenRequests = fetchTokenRequestsCase(mobileDevice.id)
//
//
//                            tokenRequests.forEach { tokenRequest ->
//                                val domain = DomainMatcher.extractDomain(tokenRequest.domain)
//                                val matchedServices = DomainMatcher.findServicesMatchingDomain(
//                                    getServicesCase(),
//                                    domain
//                                )
//
//                                runOnUiThread {
//                                    if (authenticationDialogs.containsKey(tokenRequest.requestId)
//                                            .not()
//                                    ) {
//                                        authenticationDialogs.put(
//                                            tokenRequest.requestId,
//                                            MaterialDialog(this@MainServicesActivity)
//                                                .title(text = getString(R.string.browser__2fa_token_request_title))
//                                                .message(text = getString(R.string.browser__2fa_token_request_content).plus(" ${tokenRequest.domain}?"))
//                                                .cancelable(false)
//                                                .positiveButton(text = getString(R.string.extension__approve)) {
//                                                    val isOneDomainMatched =
//                                                        matchedServices.size == 1
//                                                    val serviceId =
//                                                        if (matchedServices.size == 1) matchedServices.first().id else null
//
//                                                    if (isOneDomainMatched) {
//                                                        val payload =
//                                                            BrowserExtensionRequestPayload(
//                                                                action = BrowserExtensionRequestPayload.Action.Approve,
//                                                                notificationId = -1,
//                                                                extensionId = tokenRequest.extensionId,
//                                                                requestId = tokenRequest.requestId,
//                                                                serviceId = serviceId ?: -1,
//                                                                domain = domain,
//                                                            )
//                                                        sendBroadcast(
//                                                            BrowserExtensionRequestReceiver.createIntent(
//                                                                this@MainServicesActivity,
//                                                                payload
//                                                            )
//                                                        )
//                                                    } else {
//
//                                                        val contentIntent = Intent(
//                                                            this@MainServicesActivity,
//                                                            BrowserExtensionRequestActivity::class.java
//                                                        ).apply {
//                                                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//
//                                                            putExtra(
//                                                                BrowserExtensionRequestPayload.Key,
//                                                                BrowserExtensionRequestPayload(
//                                                                    action = BrowserExtensionRequestPayload.Action.Approve,
//                                                                    notificationId = -1,
//                                                                    extensionId = tokenRequest.extensionId,
//                                                                    requestId = tokenRequest.requestId,
//                                                                    serviceId = serviceId ?: -1,
//                                                                    domain = domain,
//                                                                )
//                                                            )
//                                                        }
//
//                                                        startActivity(contentIntent)
//                                                    }
//                                                }
//                                                .negativeButton(text = getString(R.string.extension__deny)) {
//                                                    val payload = BrowserExtensionRequestPayload(
//                                                        action = BrowserExtensionRequestPayload.Action.Deny,
//                                                        notificationId = -1,
//                                                        extensionId = tokenRequest.extensionId,
//                                                        requestId = tokenRequest.requestId,
//                                                        serviceId = -1,
//                                                        domain = domain,
//                                                    )
//                                                    sendBroadcast(
//                                                        BrowserExtensionRequestReceiver.createIntent(
//                                                            this@MainServicesActivity,
//                                                            payload
//                                                        )
//                                                    )
//                                                }
//                                                .show { }
//                                        )
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
                }
            }
        }
    }

    override fun onAuthenticated() {
        presenter.startObservingPushes()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopObservingPushes()
        authenticationDialogs.forEach { it.value.dismiss() }
        authenticationDialogs.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return


        if (requestCode == AddServiceQrActivity.REQUEST_CODE) {
            val isFromGallery =
                data?.getBooleanExtra(AddServiceQrActivity.RESULT_IS_FROM_GALLERY, false) ?: false
            data?.getParcelableExtra<ServiceDto>(AddServiceQrActivity.RESULT_SERVICE)?.let {
            }
            return
        }
    }

    override fun showRemoveQrReminder(serviceDto: ServiceDto) {
        val desc = Spanner()
            .append(getString(R.string.tokens__gallery_advice_content_first))
            .append(getString(R.string.tokens__gallery_advice_content_middle_bold), Spans.bold())
            .append(getString(R.string.tokens__gallery_advice_content_last))

        removeQrReminderDialog.show(
            title = getString(R.string.tokens__gallery_advice_title),
            desc = "",
            descSpan = desc,
            okText = getString(R.string.commons__got_it),
            imageRes = R.drawable.remove_qr_reminder_image,
            showCancel = false,
            action = { removeQrReminderDialog.dismiss() },
            actionDismiss = { },
        )
    }

    override fun showRateApp() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val flow = manager.launchReviewFlow(this, task.result)
                flow.addOnCompleteListener {
                    presenter.onReviewSuccess()
                }
            } else {
                presenter.onReviewFailed(task.exception)
            }
        }
    }

    override fun showUpgradeAppNoticeDialog(action: () -> Unit) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.update_app_title),
            msg = getString(R.string.update_app_msg),
            positiveButtonText = "Update",
            negativeButtonText = "Later",
        ).show(
            confirmAction = { action() }
        )
    }

    override fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction) {
        ConfirmDialog(this, R.string.commons__warning, R.string.tokens__service_already_exists)
            .show(confirmAction = confirmAction, cancelAction = cancelAction)
    }
}
