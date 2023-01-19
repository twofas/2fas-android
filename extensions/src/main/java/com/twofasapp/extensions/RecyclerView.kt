package com.twofasapp.extensions

fun androidx.recyclerview.widget.RecyclerView.init(
    adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>,
    animator: androidx.recyclerview.widget.SimpleItemAnimator? = androidx.recyclerview.widget.DefaultItemAnimator(),
    manager: androidx.recyclerview.widget.RecyclerView.LayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
) {

    init(adapter, animator, manager, null)
}

fun androidx.recyclerview.widget.RecyclerView.init(
    adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>,
    animator: androidx.recyclerview.widget.SimpleItemAnimator? = androidx.recyclerview.widget.DefaultItemAnimator(),
    divider: Int,
    manager: androidx.recyclerview.widget.RecyclerView.LayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
) {

    init(adapter, animator, manager, androidx.recyclerview.widget.DividerItemDecoration(context, divider))
}

fun androidx.recyclerview.widget.RecyclerView.init(
    adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>,
    animator: androidx.recyclerview.widget.SimpleItemAnimator? = androidx.recyclerview.widget.DefaultItemAnimator(),
    manager: androidx.recyclerview.widget.RecyclerView.LayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context),
    vararg decorators: androidx.recyclerview.widget.RecyclerView.ItemDecoration?
) {

    this.layoutManager = manager
    this.itemAnimator = animator
    this.adapter = adapter

    decorators.forEach {
        it?.let { addItemDecoration(it) }
    }
}
