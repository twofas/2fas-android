package com.twofasapp.prefs.model

import android.content.Context
import android.graphics.Color
import com.twofasapp.prefs.R
import com.twofasapp.prefs.isNight

enum class Tint(val displayName: String, val hex: String, val hexDark: String = hex) {
    Default("Default", "#ebebeb", "#232428"),
    LightBlue("Light blue", "#7F9CFF"),
    Indigo("Indigo", "#5E5CE6"),
    Purple("Purple", "#D95DDC"),
    Turquoise("Turquoise", "#2FCFBC"),
    Green("Green", "#03BF38"),
    Red("Red", "#ED1C24"),
    Orange("Orange", "#FF7A00"),
    Yellow("Yellow", "#FFBA0A");
}

fun Tint?.toColor(context: Context, default: Tint = Tint.Default): Int {
    return Color.parseColor(
        if (context.isNight()) (this ?: default).hexDark else (this
            ?: default).hex
    )
}

fun Tint?.toBackgroundResource(context: Context, default: Tint = Tint.Default): Int {
    return when (this) {
        Tint.LightBlue -> R.drawable.circle_lightblue
        Tint.Indigo -> R.drawable.circle_indigo
        Tint.Purple -> R.drawable.circle_purple
        Tint.Turquoise -> R.drawable.circle_turquoise
        Tint.Green -> R.drawable.circle_green
        Tint.Red -> R.drawable.circle_red
        Tint.Orange -> R.drawable.circle_orange
        Tint.Yellow -> R.drawable.circle_yellow
        Tint.Default,
        null -> if (context.isNight()) R.drawable.circle_default_night else R.drawable.circle_default
    }
}