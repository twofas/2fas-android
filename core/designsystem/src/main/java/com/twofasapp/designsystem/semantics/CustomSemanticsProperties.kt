package com.twofasapp.designsystem.semantics

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

object CustomSemanticsProperties {
    val FieldError: SemanticsPropertyKey<String> = SemanticsPropertyKey(name = "FieldError")
}

var SemanticsPropertyReceiver.fieldError by CustomSemanticsProperties.FieldError
