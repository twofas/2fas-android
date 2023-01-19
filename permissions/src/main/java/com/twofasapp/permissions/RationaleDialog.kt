package com.twofasapp.permissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.twofasapp.resources.R

class RationaleDialog(context: Context) {

    private val dialog: MaterialDialog by lazy {
        MaterialDialog(context)
            .title(text = "")
            .message(text = "")
            .positiveButton(R.string.settings__settings) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            .negativeButton(R.string.commons__cancel)
    }

    fun show(titleRes: Int, messageRes: Int) {
        dialog.title(titleRes)
        dialog.message(messageRes)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}