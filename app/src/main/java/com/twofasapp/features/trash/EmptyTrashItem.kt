package com.twofasapp.features.trash

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemEmptyTrashBinding

class EmptyTrashItem : AbstractBindingItem<ItemEmptyTrashBinding>() {

    override var identifier = R.id.item_empty_trash.toLong()
    override val type = R.id.item_empty_trash
    override var isSelectable = false

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEmptyTrashBinding.inflate(inflater, parent, false)
}