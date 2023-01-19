package com.twofasapp.design.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.twofasapp.resources.R

class InfoDialog(
    context: Context,
    titleRes: Int? = null,
    msgRes: Int? = null,
    title: String? = null,
    msg: String? = null,
    closeAction: () -> Unit = {},
) {

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .title(text = title ?: titleRes?.let { context.getString(it) } ?: "")
            .message(text = msg ?: msgRes?.let { context.getString(it) } ?: "")
            .positiveButton(R.string.commons__OK)
            .onDismiss { closeAction.invoke() }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}