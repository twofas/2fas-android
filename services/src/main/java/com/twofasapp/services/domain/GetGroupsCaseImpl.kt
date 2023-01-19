package com.twofasapp.services.domain

import com.twofasapp.prefs.model.Groups
import com.twofasapp.services.data.GroupsRepository

class GetGroupsCaseImpl(
    private val groupsRepository: GroupsRepository,
) : GetGroupsCase {

    override suspend fun invoke(): Groups {
        return groupsRepository.getGroups()
    }
}