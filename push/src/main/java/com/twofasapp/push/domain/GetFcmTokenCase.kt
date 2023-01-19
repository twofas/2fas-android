package com.twofasapp.push.domain

interface GetFcmTokenCase {
    suspend operator fun invoke(): String
}