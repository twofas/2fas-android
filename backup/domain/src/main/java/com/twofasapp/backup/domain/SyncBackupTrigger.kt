package com.twofasapp.backup.domain

enum class SyncBackupTrigger {
    FIRST_CONNECT,
    SERVICES_CHANGED,
    GROUPS_CHANGED,
    APP_START,
    APP_BACKGROUND,
    ENTER_PASSWORD,
    SET_PASSWORD,
    REMOVE_PASSWORD,
    WIPE_DATA,
}