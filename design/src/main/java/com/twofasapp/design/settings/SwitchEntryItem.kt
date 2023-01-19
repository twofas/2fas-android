package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.design.databinding.ItemEntrySwitchBinding

class SwitchEntryItem(model: SwitchEntry) : ModelAbstractBindingItem<SwitchEntry, ItemEntrySwitchBinding>(model) {

    override var identifier = model.title.hashCode().toLong() + model.isEnabled.hashCode()
    override val type = R.id.item_entry_switch

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEntrySwitchBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemEntrySwitchBinding, payloads: List<Any>) {
        binding.entryItem.update { model }
    }
}