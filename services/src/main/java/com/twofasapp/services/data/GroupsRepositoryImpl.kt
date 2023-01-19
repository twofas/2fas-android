package com.twofasapp.services.data

import com.twofasapp.prefs.model.Groups
import com.twofasapp.prefs.usecase.GroupsPreference
import com.twofasapp.services.data.GroupsRepository

class GroupsRepositoryImpl(
    private val groupsPreference: GroupsPreference,
) : GroupsRepository {

    override suspend fun getGroups(): Groups {
        return groupsPreference.get()
    }
}