package com.twofasapp.services.data

import com.twofasapp.prefs.model.Groups

interface GroupsRepository {
    suspend fun getGroups(): Groups
}