package com.twofasapp.prefs.model

import android.content.Context
import android.graphics.Color
import com.twofasapp.prefs.isNight

enum class Tint(val hex: String, val hexDark: String = hex) {
    Default("#ebebeb", "#232428"),
    Red("#ED1C24"),
    Orange("#FF7A00"),
    Yellow("#FFBA0A"),
    Green("#03BF38"),
    Turquoise("#2FCFBC"),
    LightBlue("#7F9CFF"),
    Indigo("#5E5CE6"),
    Pink("#ca49de"),
    Purple("#8C49DE"),
    Brown("#bd8857"),
    ;
}

fun Tint?.toColor(context: Context, default: Tint = Tint.Default): Int {
    return Color.parseColor(
        if (context.isNight()) (this ?: default).hexDark else (this
            ?: default).hex
    )
}