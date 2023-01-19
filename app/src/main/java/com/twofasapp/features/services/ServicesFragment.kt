package com.twofasapp.features.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.twofasapp.resources.R
import com.twofasapp.base.BaseFragmentPresenter
import com.twofasapp.core.log.FileLogger
import com.twofasapp.databinding.FragmentServicesBinding
import com.twofasapp.design.dialogs.ConfirmDialog
import com.twofasapp.design.dialogs.SimpleInputDialog
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel
import com.twofasapp.extensions.init
import com.twofasapp.extensions.isPhone
import com.twofasapp.extensions.isTablet
import com.twofasapp.extensions.isTabletLandscape
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeVisible
import com.twofasapp.features.main.MainContract
import org.koin.android.ext.android.get
import java.util.Collections

class ServicesFragment : BaseFragmentPresenter<FragmentServicesBinding>(), ServicesContract.View,
    ItemTouchCallback {

    private val presenter: ServicesContract.Presenter by injectThis()
    private val mainPresenter: MainContract.Presenter by lazy { get() }
    private val adapter = FastItemAdapter<IItem<*>>()
    private var touchHelper: ItemTouchHelper? = null
    private val confirmGroupDelete: ConfirmDialog by lazy {
        ConfirmDialog(
            requireContext(),
            title = "Delete",
            msg = getString(R.string.groups_delete_msg)
        )
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FileLogger.logScreen("Services")
        setPresenter(presenter)
        setHasOptionsMenu(true)

        viewBinding.recycler.init(
            adapter,
            DefaultItemAnimator(),
            getLayoutManager(),
        )

        val dragCallback = ServiceDragCallback(isPhone(), this)
        touchHelper = ItemTouchHelper(dragCallback)
        touchHelper?.attachToRecyclerView(viewBinding.recycler)
    }

    override fun showServiceBottomSheet(model: ServiceModel) {
        mainPresenter.showServiceBottomSheet(model.service, isFromGallery = false)
    }

    override fun showSnackbarShort(message: Int) {
        mainPresenter.showSnackbarShort(message)
    }

    override fun onDragTouch(serviceModel: ServiceModel, viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    override fun onDragTouch(groupModel: GroupModel, viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    override fun setItems(services: List<IItem<*>>) {
        viewBinding.loadingState.makeGone()
        viewBinding.recycler.makeVisible()
        FastAdapterDiffUtil.set(adapter.itemAdapter, services, ServicesDiffUtilCallback())
    }

    override fun copyToClipboard(text: String) {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Service Code", text))
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
                    is BackupNoticeItem -> spanCount
                    is GroupItem -> spanCount
                    is NoServicesItem -> spanCount
                    is NoServicesMatchingQueryItem -> spanCount
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

    override fun showContentState() {
        viewBinding.loadingState.makeGone()
        viewBinding.recycler.makeVisible()
    }

    override fun showEditGroupDialog(
        group: com.twofasapp.prefs.model.Group,
        onSaveAction: (com.twofasapp.prefs.model.Group, String) -> Unit
    ) {
        SimpleInputDialog(requireContext()).show(
            hint = getString(R.string.tokens__group_name),
            allowEmpty = false,
            maxLength = 32,
            okAction = { onSaveAction.invoke(group, it) },
            preFill = group.name
        )
    }

    override fun showConfirmGroupDelete(groupModel: GroupModel, action: (GroupModel) -> Unit) {
        confirmGroupDelete.show(
            confirmAction = { action(groupModel) }
        )
    }
}
