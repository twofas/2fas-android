package com.twofasapp.extensions

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton

fun MaterialDialog.onPositiveButtonClick(action: () -> Unit): MaterialDialog {
    getActionButton(WhichButton.POSITIVE).setOnClickListener {
        action.invoke()
        dismiss()
    }
    return this
}

fun MaterialDialog.onNegativeButtonClick(action: () -> Unit): MaterialDialog {
    getActionButton(WhichButton.NEGATIVE).setOnClickListener {
        action.invoke()
        dismiss()
    }
    return this
}