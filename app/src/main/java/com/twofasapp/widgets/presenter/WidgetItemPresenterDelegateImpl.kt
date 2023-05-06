package com.twofasapp.widgets.presenter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import com.twofasapp.entity.ServiceModel
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.isNight
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.toColor
import com.twofasapp.widgets.R
import com.twofasapp.widgets.broadcast.WidgetBroadcaster

class WidgetItemPresenterDelegateImpl(
    private val context: Context,
    private val widgetBroadcaster: WidgetBroadcaster,
) : WidgetItemPresenterDelegate {

    private val secUnit: String by lazy {
        context.getString(R.string.time_unit_seconds_short)
    }

    override fun updateItemView(
        appWidgetId: Int,
        position: Int,
        widgetService: com.twofasapp.prefs.model.Widget.Service,
        model: ServiceModel
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.layout_widget_service_item).apply {

            setTextViewText(R.id.widget_name, model.service.name)
            setTextViewText(R.id.widget_code, model.formatCode())
            setTextViewText(R.id.widget_expires, "${model.counter}$secUnit")

            setViewVisibility(R.id.widget_code, widgetService.isActive.visibleOrGone())
            setViewVisibility(R.id.widget_expires_layout, widgetService.isActive.visibleOrGone())
            setViewVisibility(R.id.widget_copy, widgetService.isActive.visibleOrGone())
            setViewVisibility(R.id.widget_chevron_active, widgetService.isActive.visibleOrGone())
            setViewVisibility(R.id.widget_chevron_inactive, widgetService.isActive.not().visibleOrGone())
            setViewVisibility(R.id.widget_name, widgetService.isActive.not().visibleOrGone())
            setOnClickFillInIntent(R.id.widget_item_root, widgetBroadcaster.intentItemClick(position))
            setOnClickFillInIntent(R.id.widget_copy, widgetBroadcaster.intentItemCopyClick(position, model.code))
            setIcon(model.service)
        }
    }

    fun RemoteViews.setIcon(service: ServiceDto) {
        val iconPath = ServiceIcons.getIcon(service.iconCollectionId, isDark = context.isNight())
        val iconInputStream = context.assets.open(iconPath)

        setImageViewBitmap(R.id.widget_icon, BitmapFactory.decodeStream(iconInputStream))

        if (service.selectedImageType == ServiceDto.ImageType.Label) {
            setTextViewText(R.id.widget_label_text, service.labelText)
            setImageViewResource(R.id.widget_label_icon, service.labelBackgroundColor.toColor(context))
        }
        setViewVisibility(R.id.widget_icon, (service.selectedImageType != ServiceDto.ImageType.Label).visibleOrGone())
        setViewVisibility(R.id.widget_label, (service.selectedImageType == ServiceDto.ImageType.Label).visibleOrGone())
    }

    private fun Boolean.visibleOrGone() = if (this) View.VISIBLE else View.GONE
}