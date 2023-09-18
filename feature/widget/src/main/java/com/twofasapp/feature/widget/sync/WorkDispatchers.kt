package com.twofasapp.feature.widget.sync

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

internal fun dispatchWidgetsUpdate(context: Context) {
    val request = OneTimeWorkRequestBuilder<WidgetsUpdateWork>().build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork("WidgetsUpdateWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
}

internal fun dispatchToggleService(context: Context, serviceId: Long) {
    val request = OneTimeWorkRequestBuilder<ToggleServiceWork>()
        .setInputData(
            Data.Builder().apply {
                putLong("serviceId", serviceId)
            }.build()
        )
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork("ToggleServiceWork", ExistingWorkPolicy.APPEND, request)
}