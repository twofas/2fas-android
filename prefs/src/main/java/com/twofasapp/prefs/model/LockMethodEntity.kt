package com.twofasapp.prefs.model

enum class LockMethodEntity {
    NO_LOCK,

    @Deprecated("Since version 2.1.10 use PIN_SECURED")
    PIN_LOCK,

    @Deprecated("Since version 2.1.10 use FINGERPRINT_WITH_PIN_SECURED")
    FINGERPRINT_LOCK,
    PIN_SECURED,
    FINGERPRINT_WITH_PIN_SECURED
}