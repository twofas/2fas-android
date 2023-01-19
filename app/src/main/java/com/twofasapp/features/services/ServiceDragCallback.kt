package com.twofasapp.features.services

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback

class ServiceDragCallback(
    isPhone: Boolean,
    itemTouchCallback: ItemTouchCallback
) : SimpleDragCallback(if (isPhone) UP_DOWN else ALL, itemTouchCallback) {

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val item = FastAdapter.getHolderAdapterItem<IItem<*>>(viewHolder)

        if ((item is ServiceItem).not()) return 0

        return super.getDragDirs(recyclerView, viewHolder)
    }

    override fun isLongPressDragEnabled() = false
}