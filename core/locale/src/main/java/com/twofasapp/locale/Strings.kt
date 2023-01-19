package com.twofasapp.locale

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
@Immutable
class Strings(c: Context) {
    val commonNext = c.getString(R.string.commons__next)
    val commonContinue = c.getString(R.string.commons__continue)
    val commonSkip = c.getString(R.string.commons__skip)

    val startupTermsLabel = c.getString(R.string.introduction__tos)
    val startupStepOneHeader = c.getString(R.string.introduction__page_1_title)
    val startupStepOneBody = c.getString(R.string.introduction__page_1_content)
    val startupStepTwoHeader = c.getString(R.string.introduction__page_2_title)
    val startupStepTwoBody = c.getString(R.string.introduction__page_2_content)
    val startupStepThreeHeader = c.getString(R.string.introduction__page_3_title)
    val startupStepThreeBody = c.getString(R.string.introduction__page_3_content)
    val startupStepFourHeader = c.getString(R.string.introduction__page_4_title)
    val startupStepFourBody = c.getString(R.string.introduction__page_4_content_android)
    val startupStartCta = c.getString(R.string.introduction__title)
}