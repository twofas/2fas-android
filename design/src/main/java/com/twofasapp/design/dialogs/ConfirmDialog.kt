package com.twofasapp.design.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.twofasapp.extensions.onNegativeButtonClick
import com.twofasapp.extensions.onPositiveButtonClick
import com.twofasapp.resources.R

typealias ConfirmAction = () -> Unit
typealias CancelAction = () -> Unit

class ConfirmDialog(
    context: Context,
    titleRes: Int = 0,
    msgRes: Int = 0,
    title: String? = null,
    msg: String? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
) {

    private var confirmAction: ConfirmAction? = null
    private var cancelAction: CancelAction? = null

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .title(text = title ?: context.getString(titleRes))
            .message(text = msg ?: context.getString(msgRes))
            .positiveButton(text = positiveButtonText ?: context.getText(R.string.commons__yes))
            .negativeButton(text = negativeButtonText ?: context.getText(R.string.commons__no))
            .cancelable(false)
            .onPositiveButtonClick { confirmAction?.invoke() }
            .onNegativeButtonClick { cancelAction?.invoke() }
    }

    fun show(title: String? = null, msg: String? = null, confirmAction: ConfirmAction? = null, cancelAction: CancelAction? = null) {
        this.confirmAction = confirmAction
        this.cancelAction = cancelAction
        dialog.show()

        title?.let { dialog.title(text = it) }
        msg?.let { dialog.message(text = it) }
    }

    fun dismiss() {
        this.confirmAction = null
        this.cancelAction = null
        dialog.dismiss()
    }
}