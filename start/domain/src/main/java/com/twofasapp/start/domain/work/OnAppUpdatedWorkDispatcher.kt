package com.twofasapp.start.domain.work

import com.twofasapp.base.work.WorkDispatcher

interface OnAppUpdatedWorkDispatcher : WorkDispatcher {
    fun dispatch()
}