package com.twofasapp.data.services.mapper

import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.WidgetService
import com.twofasapp.data.services.domain.Widgets
import com.twofasapp.prefs.model.WidgetSettingsEntity

internal fun WidgetSettingsEntity.asDomain(services: List<Service>): Widgets {
    return Widgets(
        list = widgets.map { widget ->
            Widget(
                appWidgetId = widget.appWidgetId,
                lastInteraction = widget.lastInteractionTimestamp,
                services = widget.services.mapNotNull { s ->
                    WidgetService(
                        service = services.firstOrNull { it.id == s.id } ?: return@mapNotNull null,
                        revealed = s.isActive,
                    )
                }
            )
        }
    )
}