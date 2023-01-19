package com.twofasapp.design.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

class NoticeDialog(
    val context: Context,
    val imageRes: Int,
    val titleRes: Int,
    val descRes: Int,
    val positiveRes: Int,
    val negativeRes: Int,
    val isPositiveVisible: Boolean = true,
    val isNegativeVisible: Boolean = true,
    val positiveAction: () -> Unit = {},
    val negativeAction: () -> Unit = {},
    val closeAction: () -> Unit = {},
) {

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .cancelable(true)
            .customView(view = NoticeDialogView(context), noVerticalPadding = true, scrollable = true)
    }

    fun show() {
        (dialog.getCustomView() as NoticeDialogView).setup(
            imageRes,
            titleRes,
            descRes,
            positiveRes,
            negativeRes,
            isPositiveVisible,
            isNegativeVisible,
            positiveAction = {
                positiveAction.invoke()
                dialog.dismiss()
            },
            negativeAction = {
                negativeAction.invoke()
                dialog.dismiss()
            },
            closeAction = {
                closeAction.invoke()
                dialog.dismiss()
            },
        )

        dialog.show()
    }
}