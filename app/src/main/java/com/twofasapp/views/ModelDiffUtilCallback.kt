package com.twofasapp.views

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.mikepenz.fastadapter.diff.DiffCallback

class ModelDiffUtilCallback : DiffCallback<IItem<*>> {

    override fun areContentsTheSame(oldItem: IItem<*>, newItem: IItem<*>) =
        if (oldItem is ModelAbstractBindingItem<*, *> && newItem is ModelAbstractBindingItem<*, *>) {
            oldItem.model == newItem.model
        } else {
            true
        }

    override fun areItemsTheSame(oldItem: IItem<*>, newItem: IItem<*>) =
        oldItem.identifier == newItem.identifier

    override fun getChangePayload(oldItem: IItem<*>, oldItemPosition: Int, newItem: IItem<*>, newItemPosition: Int) = Unit
}