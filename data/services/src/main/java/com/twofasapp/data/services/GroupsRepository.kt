package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Group
import kotlinx.coroutines.flow.Flow

interface GroupsRepository {
    fun observeGroups(): Flow<List<Group>>
}