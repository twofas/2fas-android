package com.twofasapp.permissions.internal

import com.twofasapp.permissions.PermissionStatus
import io.reactivex.processors.PublishProcessor

internal class PermissionProcessor {
    val publisher: PublishProcessor<PermissionStatus> = PublishProcessor.create()
}