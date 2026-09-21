package com.twofasapp.feature.home.ui.settings

import com.twofasapp.core.design.foundation.lazy.ListItem

internal sealed class SettingsListItem(key: Any? = null, type: Any? = null) : ListItem(key, type) {
    data class Header(val text: String) : SettingsListItem(key = "Header:$text", type = "Header")
    data class Entry(val text: String) : SettingsListItem(key = "Entry:$text", type = "Entry")
}