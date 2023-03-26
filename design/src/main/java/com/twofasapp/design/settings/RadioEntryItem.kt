package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.design.databinding.ItemEntryRadioBinding
import com.twofasapp.resources.R

class RadioEntryItem(model: RadioEntry) : ModelAbstractBindingItem<RadioEntry, ItemEntryRadioBinding>(model) {

    override var identifier = model.title.hashCode().toLong() + model.isEnabled.hashCode()
    override val type = R.id.item_entry_radio

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEntryRadioBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemEntryRadioBinding, payloads: List<Any>) {
        binding.entryItem.update { model }
    }
}