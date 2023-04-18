package com.twofasapp.features.backup.status

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.twofasapp.databinding.ItemSyncErrorBinding
import com.twofasapp.extensions.makeGoneIfBlank
import com.twofasapp.resources.R
import com.twofasapp.services.backup.models.RemoteBackupErrorType

class SyncErrorItem(model: Model) : ModelAbstractBindingItem<SyncErrorItem.Model, ItemSyncErrorBinding>(model) {

    data class Model(
        val type: RemoteBackupErrorType,
        val errorMsg: Int,
        val showErrorCode: Boolean,
    )

    override var identifier = model.errorMsg.toLong()
    override val type = R.id.item_sync_error

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemSyncErrorBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemSyncErrorBinding, payloads: List<Any>) {
        binding.msg.setText(model.errorMsg)
        binding.code.text = if (model.showErrorCode) "Error code: ${model.type.code}" else ""
        binding.code.makeGoneIfBlank()
    }
}