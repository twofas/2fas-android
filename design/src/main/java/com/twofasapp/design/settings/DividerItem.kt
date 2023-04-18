package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.design.databinding.ItemDividerBinding
import com.twofasapp.resources.R

class DividerItem : AbstractBindingItem<ItemDividerBinding>() {

    override val type = R.id.item_divider

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemDividerBinding.inflate(inflater, parent, false)
}