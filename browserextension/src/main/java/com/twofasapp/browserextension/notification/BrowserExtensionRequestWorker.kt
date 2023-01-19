package com.twofasapp.browserextension.notification

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.browserextension.domain.ApproveLoginRequestCase
import com.twofasapp.browserextension.domain.DenyLoginRequestCase
import com.twofasapp.serialization.JsonSerializer
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BrowserExtensionRequestWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val approveLoginRequestCase: ApproveLoginRequestCase by inject()
    private val denyLoginRequestCase: DenyLoginRequestCase by inject()
    private val generateTotp: GenerateTotp by inject()
    private val getServicesCase: GetServicesCase by inject()
    private val jsonSerializer: JsonSerializer by inject()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            val payload = inputData.getString(BrowserExtensionRequestPayload.Key)!!.let {
                jsonSerializer.deserialize<BrowserExtensionRequestPayload>(it)
            }

            launch {
                try {

                    when (payload.action) {
                        BrowserExtensionRequestPayload.Action.Approve -> {
                            val service = getServicesCase().first { it.id == payload.serviceId }

                            approveLoginRequestCase(
                                extensionId = payload.extensionId,
                                requestId = payload.requestId,
                                code = generateTotp.calculateCode(
                                    secret = service.secret,
                                    otpDigits = service.otp.digits,
                                    otpAlgorithm = service.otp.algorithm.name,
                                    otpPeriod = service.otp.period,
                                    counter = when (service.authType) {
                                        Service.AuthType.TOTP -> null
                                        Service.AuthType.HOTP -> service.otp.hotpCounter?.toLong() ?: Service.DefaultHotpCounter.toLong()
                                    }
                                ),
                            )
                            showToast("Code sent successfully")
                        }
                        BrowserExtensionRequestPayload.Action.Deny -> {
                            denyLoginRequestCase.invoke(payload.extensionId, payload.requestId)
                        }
                    }


                } catch (e: Exception) {
                    showToast("Error occurred when sending code")
                }
            }

            Result.success()
        }
    }

    private fun showToast(text: String) {
        try {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }, 500)
        } catch (_: Exception) {

        }
    }
}