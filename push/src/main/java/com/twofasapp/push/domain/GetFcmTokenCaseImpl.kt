package com.twofasapp.push.domain

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

internal class GetFcmTokenCaseImpl : GetFcmTokenCase {

    override suspend operator fun invoke(): String {
        return FirebaseMessaging.getInstance().token.await()
    }
}