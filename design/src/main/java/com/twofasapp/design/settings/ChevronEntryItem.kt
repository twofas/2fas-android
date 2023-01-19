package com.twofasapp.design.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.design.databinding.ItemEntryChevronBinding

class ChevronEntryItem(model: ChevronEntry) : ModelAbstractBindingItem<ChevronEntry, ItemEntryChevronBinding>(model) {

    override val type = R.id.item_entry_chevron

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEntryChevronBinding.inflate(inflater, parent, false)
}