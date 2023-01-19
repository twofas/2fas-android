package com.twofasapp.security.domain

interface GetPinCase {
    suspend operator fun invoke(): String
}