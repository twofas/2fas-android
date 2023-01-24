package com.twofasapp.data.services.local

import com.twofasapp.storage.PlainPreferences

internal class GroupsLocalSource(
    private val preferences: PlainPreferences,
) {
    companion object {
        private const val KeyGroups = "groups"
    }
}