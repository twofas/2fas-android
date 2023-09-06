package com.twofasapp.extensions

import androidx.fragment.app.Fragment
import com.twofasapp.resources.R

fun Fragment.isPhone() = resources.getBoolean(R.bool.isPhone)
fun Fragment.isTablet() = resources.getBoolean(R.bool.isTablet)
fun Fragment.isTabletLandscape() = resources.getBoolean(R.bool.isTabletLandscape)
fun Fragment.isNight() = resources.getBoolean(R.bool.isNight)