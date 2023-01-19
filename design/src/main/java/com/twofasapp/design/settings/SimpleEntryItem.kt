package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.design.databinding.ItemEntrySimpleBinding

class SimpleEntryItem(model: SimpleEntry) : ModelAbstractBindingItem<SimpleEntry, ItemEntrySimpleBinding>(model) {

    override var identifier = model.title.hashCode().toLong() + model.isEnabled.hashCode()
    override val type = R.id.item_entry_simple

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEntrySimpleBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemEntrySimpleBinding, payloads: List<Any>) {
        binding.entryItem.update { model }
    }
}