package com.twofasapp.storage

import com.twofasapp.storage.internal.PreferencesDelegate

internal class PlainPreferencesImpl(delegate: PreferencesDelegate) : PlainPreferences by delegate