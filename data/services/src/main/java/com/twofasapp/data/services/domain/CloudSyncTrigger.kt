package com.twofasapp.data.services.domain

enum class CloudSyncTrigger {
    FirstConnect,
    ServicesChanged,
    GroupsChanged,
    AppStart,
    AppBackground,
    EnterPassword,
    SetPassword,
    RemovePassword,
}