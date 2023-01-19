package com.twofasapp.permissions.internal

import com.twofasapp.permissions.PermissionStatus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PermissionSharedFlow {
    val flow: MutableSharedFlow<PermissionStatus> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
}