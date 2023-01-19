package com.twofasapp.features.services

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemNoServicesMatchingQueryBinding

class NoServicesMatchingQueryItem : AbstractBindingItem<ItemNoServicesMatchingQueryBinding>() {

    override var identifier = R.id.item_no_services_matching_query.toLong()
    override val type = R.id.item_no_services_matching_query
    override var isSelectable = false

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemNoServicesMatchingQueryBinding.inflate(inflater, parent, false)
}