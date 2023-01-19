package com.twofasapp.features.trash

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.resources.R
import com.twofasapp.databinding.ItemTrashedServiceBinding

class TrashedServiceItem(
    model: TrashedService,
    private val onRestoreClick: (TrashedService) -> Unit,
    private val onDeleteClick: (TrashedService) -> Unit,
) : ModelAbstractBindingItem<TrashedService, ItemTrashedServiceBinding>(model) {

    override var identifier = model.service.id
    override val type = R.id.item_trashed_service
    override var isSelectable = false

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemTrashedServiceBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemTrashedServiceBinding, payloads: List<Any>) {
        binding.name.text = model.service.name
        binding.info.text = model.service.otpAccount
        binding.info.isVisible = model.service.otpAccount.isNullOrBlank().not()

        binding.iconLayout.updateIcon(model.service)

        binding.actionMore.setOnClickListener {
            val popup = PopupMenu(it.context!!, it)
            popup.menuInflater.inflate(com.twofasapp.R.menu.menu_trashed_service, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    com.twofasapp.R.id.menu_restore -> onRestoreClick.invoke(model)
                    com.twofasapp. R.id.menu_delete -> onDeleteClick.invoke(model)
                }
                true
            }
            popup.show()
        }
    }
}