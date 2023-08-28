package com.twofasapp.design.compose.dialogs

sealed interface Validation {
    object Ok : Validation
    class Error(val msgRes: Int? = null, val msg: String? = null) : Validation
}

enum class InputType {
    Text, NumberInteger, NumberDecimal, Password
}