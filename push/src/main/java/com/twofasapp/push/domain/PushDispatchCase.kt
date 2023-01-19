package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push

internal interface PushDispatchCase {
    operator fun invoke(push: Push)
}