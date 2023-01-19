package com.twofasapp.features.trash

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.databinding.ActivityTrashBinding
import com.twofasapp.views.ModelDiffUtilCallback

class TrashActivity : BaseActivityPresenter<ActivityTrashBinding>(), TrashContract.View {

    private val presenter: TrashContract.Presenter by injectThis()
    private val adapter = FastItemAdapter<IItem<*>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityTrashBinding::inflate)
        setPresenter(presenter)
        viewBinding.recycler.adapter = adapter
        viewBinding.recycler.itemAnimator = DefaultItemAnimator()
    }

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun setItems(items: List<IItem<*>>) {
        FastAdapterDiffUtil.set(adapter.itemAdapter, items, ModelDiffUtilCallback())
    }
}