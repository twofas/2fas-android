package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.ServicesOrder
import com.twofasapp.storage.Preferences

class ServicesOrderPreference(preferences: Preferences) : PreferenceModel<ServicesOrder>(preferences) {

    override val key: String = "servicesOrder"
    override val default: ServicesOrder = ServicesOrder(type = ServicesOrder.Type.Manual)

    override val serialize: (ServicesOrder) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> ServicesOrder = { jsonSerializer.deserialize(it) }
}