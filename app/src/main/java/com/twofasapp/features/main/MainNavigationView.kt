package com.twofasapp.features.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.navigation.NavigationView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.twofasapp.resources.R
import com.twofasapp.databinding.ViewMainNavigationBinding
import com.twofasapp.design.settings.DividerItem

class MainNavigationView : NavigationView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val binding = ViewMainNavigationBinding.inflate(LayoutInflater.from(context), this)
    private val fastAdapter = FastItemAdapter<IItem<*>>()

    init {
        setBackgroundResource(R.color.drawerBackground)
        binding.recycler.adapter = fastAdapter
        binding.recycler.itemAnimator = null
    }

    fun setItems(items: List<DrawerEntry>) {
        fastAdapter.set(
            items.map { DrawerEntryItem(it) as IItem<*> }.toMutableList()
                .apply { add(5, DividerItem()) }
                .apply { add(9, DividerItem()) }
        )
    }
}