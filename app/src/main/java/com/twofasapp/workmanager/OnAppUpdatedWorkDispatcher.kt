package com.twofasapp.workmanager

import com.twofasapp.common.domain.WorkDispatcher

interface OnAppUpdatedWorkDispatcher : WorkDispatcher {
    fun dispatch()
}