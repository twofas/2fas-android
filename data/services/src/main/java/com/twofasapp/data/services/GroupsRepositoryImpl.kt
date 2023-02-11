package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.GroupsLocalSource
import com.twofasapp.data.services.mapper.asDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GroupsRepositoryImpl(
    private val local: GroupsLocalSource,
) : GroupsRepository {

    override fun observeGroups(): Flow<List<Group>> {
        return local.observeGroups().map { it.list.map { it.asDomain() } }
    }
}