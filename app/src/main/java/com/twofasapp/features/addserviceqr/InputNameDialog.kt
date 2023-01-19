package com.twofasapp.features.addserviceqr

import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.twofasapp.resources.R

typealias OnNameConfirmedAction = ((String) -> Unit)
typealias OnNameDismissAction = (() -> Unit)

class InputNameDialog(context: Context) {

    private var positiveAction: OnNameConfirmedAction? = null
    private var negativeAction: OnNameDismissAction? = null

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .title(R.string.tokens__type_service_name)
            .cancelable(true)
            .input(hint = "Name", inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, maxLength = 30, allowEmpty = false)
            .positiveButton(R.string.commons__save) {
                positiveAction?.invoke(it.getInputField().text.toString())
            }
            .negativeButton(R.string.commons__cancel) {
                negativeAction?.invoke()
            }
            .onDismiss { negativeAction?.invoke() }
    }

    fun show(action: OnNameConfirmedAction, dismiss: OnNameDismissAction) {
        this.positiveAction = action
        this.negativeAction = dismiss
        dialog.show()
    }

    fun dismiss() {
        this.positiveAction = null
        dialog.dismiss()
    }
}