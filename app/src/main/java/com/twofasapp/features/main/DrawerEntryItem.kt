package com.twofasapp.features.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemDrawerEntryBinding
import com.twofasapp.extensions.animateGone
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeInvisible
import com.twofasapp.extensions.makeVisible

class DrawerEntryItem(model: DrawerEntry) : ModelAbstractBindingItem<DrawerEntry, ItemDrawerEntryBinding>(model) {

    override val type = R.id.item_drawer_entry

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemDrawerEntryBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemDrawerEntryBinding, payloads: List<Any>) {
        binding.icon.setImageResource(model.iconRes)
        binding.title.setText(model.titleRes)
        binding.root.isSelected = model.isSelected
        binding.root.setOnClickListener { model.onClick.invoke() }

        binding.endIcon.isVisible = model.endIconRes != null
        model.endIconRes?.let { binding.endIcon.setImageResource(it) }

        when (model.badge) {
            DrawerEntry.Badge.None -> {
                binding.badgeDot.makeGone()
                binding.badgeDotIcon.makeInvisible()
                binding.badgeLabel.animateGone()
            }

            DrawerEntry.Badge.Dot -> {
                binding.badgeDot.makeVisible()
                binding.badgeDotIcon.makeInvisible()
                binding.badgeLabel.makeGone()
            }

            DrawerEntry.Badge.DotIcon -> {
                binding.badgeDot.makeGone()
                binding.badgeDotIcon.makeVisible()
                binding.badgeLabel.makeGone()
            }

            is DrawerEntry.Badge.Label -> {
                binding.badgeDot.makeGone()
                binding.badgeDotIcon.makeInvisible()
                binding.badgeLabel.text = (model.badge as DrawerEntry.Badge.Label).text
                binding.badgeLabel.alpha = 1f
                binding.badgeLabel.makeVisible()
            }
        }
    }
}