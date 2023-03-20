package com.twofasapp.features.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.twofasapp.base.BaseFragmentPresenter
import com.twofasapp.databinding.FragmentServicesBinding
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel
import com.twofasapp.extensions.isPhone
import com.twofasapp.extensions.isTablet
import com.twofasapp.extensions.isTabletLandscape
import java.util.Collections

class ServicesFragment : BaseFragmentPresenter<FragmentServicesBinding>(), ServicesContract.View,
    ItemTouchCallback {

    private val presenter: ServicesContract.Presenter by injectThis()
    private val adapter = FastItemAdapter<IItem<*>>()
    private var touchHelper: ItemTouchHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(
            inflater,
            container,
            savedInstanceState,
            FragmentServicesBinding::inflate
        )
    }

    override fun onDragTouch(serviceModel: ServiceModel, viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    override fun onDragTouch(groupModel: GroupModel, viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        val spanCount =
            when {
                isPhone() -> 1
                isTablet() -> 2
                isTabletLandscape() -> 3
                else -> 1
            }

        if (spanCount == 1) {
            return LinearLayoutManager(activity)
        }

        val gridLayoutManager = GridLayoutManager(activity, spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                when (adapter.getAdapterItem(position)) {
                    is GroupItem -> spanCount
                    else -> 1
                }
        }

        return gridLayoutManager
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        val oldItem = adapter.adapterItems[oldPosition]

        if (oldItem is ServiceItem) {
            Collections.swap(adapter.adapterItems, oldPosition, newPosition)
            adapter.notifyAdapterItemMoved(oldPosition, newPosition)
            presenter.changeServiceOrder(oldItem, newPosition, adapter.adapterItems)
        }

        if (oldItem is GroupItem) {
            val oldOrder =
                adapter.adapterItems.filterIsInstance<GroupItem>().map { it.model.group.id }
            val newOrder = adapter.adapterItems
                .toMutableList()
                .apply {
                    val itemFromNewPosition = get(newPosition)
                    set(newPosition, oldItem)
                    set(oldPosition, itemFromNewPosition)
                }
                .filterIsInstance<GroupItem>().map { it.model.group.id }

            if ((oldOrder == newOrder).not()) {
                Collections.swap(adapter.adapterItems, oldPosition, newPosition)
                adapter.notifyAdapterItemMoved(oldPosition, newPosition)
                presenter.changeGroupOrder(oldItem, newPosition, adapter.adapterItems)
            }
        }

        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) = Unit
}
