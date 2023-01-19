package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.MobileDeviceEntity

class MobileDevicePreference(preferences: Preferences) : PreferenceModel<MobileDeviceEntity>(preferences) {

    override val key: String = "mobileDevice"
    override val default: MobileDeviceEntity = MobileDeviceEntity(
        id = "",
        name = "",
        fcmToken = "",
        platform = "",
        publicKey = "",
    )

    override val serialize: (MobileDeviceEntity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> MobileDeviceEntity = { jsonSerializer.deserialize(it) }
}