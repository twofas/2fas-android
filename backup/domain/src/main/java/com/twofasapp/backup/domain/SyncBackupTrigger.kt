package com.twofasapp.backup.domain

enum class SyncBackupTrigger {
    FirstConnect,
    ServicesChanged,
    GroupsChanged,
    AppStart,
    AppBackground,
    EnterPassword,
    SetPassword,
    RemovePassword,
}