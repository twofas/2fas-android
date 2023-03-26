package com.twofasapp.usecases.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.twofasapp.base.usecase.UseCase
import com.twofasapp.entity.ConnectionStatus
import io.reactivex.Scheduler

class CheckConnectionStatus(
    private val context: Context
) : UseCase<ConnectionStatus> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): ConnectionStatus {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        return ConnectionStatus(isConnected = activeNetwork?.isConnectedOrConnecting == true)
    }
}