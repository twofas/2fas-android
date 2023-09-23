package com.twofasapp.browserextension.notification

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.browserextension.domain.ApproveLoginRequestCase
import com.twofasapp.browserextension.domain.DenyLoginRequestCase
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.serialization.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
    private val servicesRepository: ServicesRepository by inject()
    private val jsonSerializer: JsonSerializer by inject()
    private val browserExtRepository: BrowserExtRepository by inject()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            val payload = inputData.getString(BrowserExtensionRequestPayload.Key)!!.let {
                jsonSerializer.deserialize<BrowserExtensionRequestPayload>(it)
            }

            launch {
                try {
                    when (payload.action) {
                        BrowserExtensionRequestPayload.Action.Approve -> {
                            val service = servicesRepository.observeServicesWithCode().first()
                                .first { it.id == payload.serviceId }

                            approveLoginRequestCase(
                                extensionId = payload.extensionId,
                                requestId = payload.requestId,
                                code = service.code?.current.orEmpty(),
                            )

                            showToast(context.getString(com.twofasapp.resources.R.string.extension__code_sent_msg))
                        }

                        BrowserExtensionRequestPayload.Action.Deny -> {
                            denyLoginRequestCase.invoke(payload.extensionId, payload.requestId)
                        }
                    }

                    browserExtRepository.deleteTokenRequest(payload.requestId)

                } catch (e: Exception) {
                    showToast(context.getString(com.twofasapp.resources.R.string.extension__code_sent_error_msg))
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