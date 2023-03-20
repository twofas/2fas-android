package com.twofasapp.features.services

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel

interface ServicesContract {

    interface View {
        fun onDragTouch(serviceModel: ServiceModel, viewHolder: RecyclerView.ViewHolder)
        fun onDragTouch(groupModel: GroupModel, viewHolder: RecyclerView.ViewHolder)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun changeServiceOrder(serviceItem: ServiceItem, newPosition: Int, items: List<IItem<*>>)
        abstract fun changeGroupOrder(groupItem: GroupItem, newPosition: Int, items: List<IItem<*>>)
    }
}