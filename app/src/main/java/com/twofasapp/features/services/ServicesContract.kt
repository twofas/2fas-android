package com.twofasapp.features.services

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.twofasapp.prefs.model.Group
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel

interface ServicesContract {

    interface View {
        fun setItems(services: List<IItem<*>>)
        fun copyToClipboard(text: String)
        fun showContentState()
        fun showSnackbarShort(message: Int)
        fun onDragTouch(serviceModel: ServiceModel, viewHolder: RecyclerView.ViewHolder)
        fun onDragTouch(groupModel: GroupModel, viewHolder: RecyclerView.ViewHolder)
        fun showEditGroupDialog(group: Group, onSaveAction: (Group, String) -> Unit)
        fun showConfirmGroupDelete(groupModel: GroupModel, action: (GroupModel) -> Unit)
        fun showServiceBottomSheet(model: ServiceModel)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun changeServiceOrder(serviceItem: ServiceItem, newPosition: Int, items: List<IItem<*>>)
        abstract fun changeGroupOrder(groupItem: GroupItem, newPosition: Int, items: List<IItem<*>>)
    }
}