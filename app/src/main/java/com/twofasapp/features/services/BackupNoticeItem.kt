package com.twofasapp.features.services

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemBackupNoticeBinding

class BackupNoticeItem(
    private val onTurnOnClick: () -> Unit,
    private val onDismissClick: () -> Unit
) : AbstractBindingItem<ItemBackupNoticeBinding>() {

    override var identifier = R.id.item_backup_notice.toLong()

    override val type = R.id.item_backup_notice

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemBackupNoticeBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemBackupNoticeBinding, payloads: List<Any>) {
        binding.maybeLater.setOnClickListener { onDismissClick() }
        binding.turnOn.setOnClickListener { onTurnOnClick() }
    }
}
