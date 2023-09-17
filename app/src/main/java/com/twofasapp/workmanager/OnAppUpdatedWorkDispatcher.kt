package com.twofasapp.workmanager

interface OnAppUpdatedWorkDispatcher : com.twofasapp.di.WorkDispatcher {
    fun dispatch()
}