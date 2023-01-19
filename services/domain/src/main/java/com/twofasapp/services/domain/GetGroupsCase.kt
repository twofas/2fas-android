package com.twofasapp.services.domain

import com.twofasapp.prefs.model.Groups

interface GetGroupsCase {
    suspend operator fun invoke(): Groups
}