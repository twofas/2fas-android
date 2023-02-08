package com.twofasapp.backup.ui.export

import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.twofasapp.backup.databinding.DialogExportBackupPasswordBinding
import com.twofasapp.extensions.onTextChanged
import com.twofasapp.resources.R

class ExportBackupPasswordDialog(
    private val context: Context,
    private val listener: Listener
) {

    interface Listener {
        fun onPasswordDialogSaved(password: String)
        fun onPasswordDialogCanceled()
    }

    private lateinit var binding: DialogExportBackupPasswordBinding
    private var password: String = ""
    private var passwordRepeat: String = ""

    private val dialog: MaterialDialog by lazy {
        binding = DialogExportBackupPasswordBinding.inflate(LayoutInflater.from(context))

        MaterialDialog(context)
            .title(res = R.string.backup_settings_password_set_title)
            .message(res = R.string.backup__set_password_title)
            .customView(view = binding.root, noVerticalPadding = true, horizontalPadding = false)
            .onPreShow {
                binding.passwordEditText.post {
                    binding.passwordEditText.requestFocus()
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.passwordEditText, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            .cancelable(false)
    }


    fun show() {
        dialog.show()

        binding.cancel.setOnClickListener {
            password = ""
            passwordRepeat = ""
            listener.onPasswordDialogCanceled()
            dismiss()
        }

        binding.save.isEnabled = false
        binding.save.setOnClickListener {
            listener.onPasswordDialogSaved(password)
            dismiss()
        }

        binding.passwordEditText.setText(password)
        binding.passwordRepeatEditText.setText(passwordRepeat)

        binding.passwordEditText.onTextChanged {
            password = it
            validatePassword()
        }
        binding.passwordRepeatEditText.onTextChanged {
            passwordRepeat = it
            validatePassword()
        }

        if (password.isNotBlank()) {
            validatePassword()
        }
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun validatePassword() {
        val isValid = password.isNotBlank()
                && password.length >= 3
                && password.equals(passwordRepeat, false)
                && password.matches(Regex("([A-Za-z0-9*.!@#\$%^&(){}\\[\\]:;<>,?/~_+-=|']+)"))
                && passwordRepeat.matches(Regex("([A-Za-z0-9*.!@#\$%^&(){}\\[\\]:;<>,?/~_+-=|']+)"))

        binding.save.isEnabled = isValid

        binding.passwordLayout.error = when {
            password.length < 3 -> context.getString(R.string.backup__to_short_error)
            password.matches(Regex("([A-Za-z0-9*.!@#\$%^&(){}\\[\\]:;<>,?/~_+-=|']+)"))
                .not() -> context.getString(R.string.backup__incorrect_character_error)

            else -> null
        }

        binding.passwordRepeatLayout.error = when {
            passwordRepeat.isEmpty() -> null
            passwordRepeat.length < 3 -> context.getString(R.string.backup__to_short_error)
            passwordRepeat.matches(Regex("([A-Za-z0-9*.!@#\$%^&(){}\\[\\]:;<>,?/~_+-=|']+)"))
                .not() -> context.getString(R.string.backup__incorrect_character_error)

            password.equals(passwordRepeat, false).not() -> context.getString(R.string.backup__passwords_dont_match)
            else -> null
        }
    }
}