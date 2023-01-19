package com.twofasapp.features.addserviceqr

import android.content.Context
import android.text.Spannable
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

typealias OkAction = (() -> Unit)
typealias DismissAction = (() -> Unit)

class ScanInfoDialog(context: Context) {

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .customView(view = ScanInfoDialogView(context), noVerticalPadding = true, scrollable = true)
    }

    fun show(
        title: String,
        desc: String,
        descSpan: Spannable? = null,
        okText: String,
        imageRes: Int,
        showCancel: Boolean = true,
        action: OkAction,
        actionDismiss: DismissAction = {}
    ) {
        dialog.show()
        (dialog.getCustomView() as ScanInfoDialogView).setup(
            title = title,
            desc = desc,
            descSpan = descSpan,
            okText = okText,
            imageRes = imageRes,
            showCancel = showCancel,
            okAction = {
                action.invoke()
                dialog.dismiss()
            },
            cancelAction = { dialog.dismiss() }
        )

        dialog.setOnDismissListener { actionDismiss.invoke() }
    }

    fun dismiss() {
        dialog.dismiss()
    }
}