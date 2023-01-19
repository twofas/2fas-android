package com.twofasapp.storage

import com.twofasapp.storage.internal.PreferencesDelegate

internal class EncryptedPreferencesImpl(delegate: PreferencesDelegate) : EncryptedPreferences by delegate