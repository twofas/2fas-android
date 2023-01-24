package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GroupsRepositoryImpl : GroupsRepository {
    override fun observeGroups(): Flow<List<Group>> {
        return flow { }
    }
}