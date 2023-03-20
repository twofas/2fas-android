package com.twofasapp.start.domain.work

interface OnAppUpdatedWorkDispatcher : com.twofasapp.di.WorkDispatcher {
    fun dispatch()
}