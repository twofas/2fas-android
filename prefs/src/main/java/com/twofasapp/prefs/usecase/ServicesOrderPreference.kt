package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.ServicesOrder
import com.twofasapp.storage.Preferences
import kotlinx.serialization.encodeToString

class ServicesOrderPreference(preferences: Preferences) : PreferenceModel<ServicesOrder>(preferences) {

    override val key: String = "servicesOrder"
    override val default: ServicesOrder = ServicesOrder(type = ServicesOrder.Type.Manual)

    override val serialize: (ServicesOrder) -> String = { jsonSerializer.encodeToString(it) }
    override val deserialize: (String) -> ServicesOrder = { jsonSerializer.decodeFromString(it) }
}