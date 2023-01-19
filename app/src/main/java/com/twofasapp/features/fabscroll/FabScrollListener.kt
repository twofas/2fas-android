package com.twofasapp.features.fabscroll

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabScrollListener(private val fab: FloatingActionButton) : RecyclerView.OnScrollListener() {

    var isForceHidden = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (isForceHidden) {
            return
        }

        if (isRecyclerViewFullyVisible(recyclerView)) {
            fab.show()
            return
        }

        if (dy > 0 && fab.visibility == View.VISIBLE) {
            fab.hide()
        } else if (dy < 0 && fab.visibility != View.VISIBLE) {
            fab.show()
        }
    }

    private fun isRecyclerViewFullyVisible(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val adapter = recyclerView.adapter

        if (adapter == null || adapter.itemCount == 0) {
            return true
        }

        return (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) && layoutManager.findFirstCompletelyVisibleItemPosition() == 0
    }
}
