package com.twofasapp.features.backup.status

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.twofasapp.databinding.ViewTurnOffBackupConfirmationBinding
import com.twofasapp.extensions.dpToPx

class TurnOffBackupConfirmation : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val binding = ViewTurnOffBackupConfirmationBinding.inflate(LayoutInflater.from(context), this)

    init {
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        orientation = VERTICAL
    }

    fun setup(confirmAction: () -> Unit, cancelAction: () -> Unit, closeAction: () -> Unit) {
        binding.confirm.setOnClickListener { confirmAction.invoke() }
        binding.cancel.setOnClickListener { cancelAction.invoke() }
        binding.close.setOnClickListener { closeAction.invoke() }
    }
}