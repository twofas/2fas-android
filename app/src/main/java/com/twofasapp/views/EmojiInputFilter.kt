package com.twofasapp.views

import android.text.InputFilter
import android.text.Spanned

object EmojiInputFilter : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        for (index in start until end) {
            source?.let {
                val type = Character.getType(it[index])

                if (type == Character.SURROGATE.toInt()) {
                    return ""
                }
            }
        }
        return null
    }
}