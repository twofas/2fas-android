package com.twofasapp.prefs

import android.app.Activity
import android.content.Context

fun Activity.isNight() = resources.getBoolean(R.bool.isNight)
fun Context.isNight() = resources.getBoolean(R.bool.isNight)

