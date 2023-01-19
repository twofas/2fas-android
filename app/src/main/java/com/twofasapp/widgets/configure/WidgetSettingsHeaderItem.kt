package com.twofasapp.widgets.configure

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemWidgetSettingsHeaderBinding

class WidgetSettingsHeaderItem : AbstractBindingItem<ItemWidgetSettingsHeaderBinding>() {

    override val type = R.id.item_widget_settings_header
    override var identifier = R.id.item_widget_settings_header.toLong()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemWidgetSettingsHeaderBinding.inflate(inflater, parent, false)
}