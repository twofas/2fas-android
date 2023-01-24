package com.twofasapp.features.services

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.databinding.ItemNoServicesBinding
import com.twofasapp.resources.R

class NoServicesItem : AbstractBindingItem<ItemNoServicesBinding>() {

    override var identifier = R.id.item_no_services.toLong()
    override val type = R.id.item_no_services
    override var isSelectable = false

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemNoServicesBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemNoServicesBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
    }
}