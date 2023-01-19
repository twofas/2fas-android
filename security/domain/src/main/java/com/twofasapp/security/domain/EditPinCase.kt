package com.twofasapp.security.domain

interface EditPinCase {
    suspend operator fun invoke(pin: String)
}