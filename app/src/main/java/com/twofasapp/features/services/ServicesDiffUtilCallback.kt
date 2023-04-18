package com.twofasapp.features.services

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.mikepenz.fastadapter.diff.DiffCallback

class ServicesDiffUtilCallback : DiffCallback<IItem<*>> {

    override fun areContentsTheSame(oldItem: IItem<*>, newItem: IItem<*>): Boolean {
//        if (oldItem is BackupNoticeItem && newItem is BackupNoticeItem) {
//            return true
//        }
//
//        if (oldItem is NoServicesItem && newItem is NoServicesItem) {
//            return true
//        }

        if (oldItem is ServiceItem && newItem is ServiceItem) {
            return oldItem.model == newItem.model
                    && oldItem.isInEditMode == newItem.isInEditMode
                    && oldItem.isInSearchMode == newItem.isInSearchMode
        }

        if (oldItem is GroupItem && newItem is GroupItem) {
            return oldItem.model == newItem.model && oldItem.isInEditMode == newItem.isInEditMode && oldItem.model.services.size == newItem.model.services.size
                    && oldItem.isFirst == newItem.isFirst
                    && oldItem.isLast == newItem.isLast
                    && oldItem.isInSearchMode == newItem.isInSearchMode
        }

        return if (oldItem is ModelAbstractBindingItem<*, *> && newItem is ModelAbstractBindingItem<*, *>) {
            oldItem.model == newItem.model
        } else {
            false
        }
    }

    override fun areItemsTheSame(oldItem: IItem<*>, newItem: IItem<*>) =
        oldItem.identifier == newItem.identifier

    override fun getChangePayload(oldItem: IItem<*>, oldItemPosition: Int, newItem: IItem<*>, newItemPosition: Int) = Unit
}