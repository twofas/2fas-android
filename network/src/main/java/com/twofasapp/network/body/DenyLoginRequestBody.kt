package com.twofasapp.network.body

import kotlinx.serialization.Serializable

@Serializable
class DenyLoginRequestBody(
    val status: String = "completed"
)