package com.twofasapp.widgets.presenter

import android.content.Intent

interface WidgetReceiverPresenterDelegate {
    fun onReceive(intent: Intent)
    fun onDeleted()
    fun onDataSetChanged()
}