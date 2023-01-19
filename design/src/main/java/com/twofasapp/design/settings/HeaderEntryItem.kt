package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.design.databinding.ItemEntryHeaderBinding

class HeaderEntryItem(model: HeaderEntry) : ModelAbstractBindingItem<HeaderEntry, ItemEntryHeaderBinding>(model) {

    override var identifier = model.hashCode().toLong()
    override val type = R.id.item_entry_header

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEntryHeaderBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemEntryHeaderBinding, payloads: List<Any>) {
        binding.header.update { model }
    }
}