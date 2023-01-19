package com.twofasapp.design.dialogs

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.textfield.TextInputLayout
import com.twofasapp.design.databinding.DialogSimpleInputBinding
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeVisible
import com.twofasapp.extensions.onTextChanged

class SimpleInputDialog(private val context: Context) {

    private lateinit var binding: DialogSimpleInputBinding
    private lateinit var dialog: MaterialDialog

    fun show(
        title: String? = null,
        description: String? = null,
        okText: String? = null,
        hint: String,
        preFill: String? = null,

        allowEmpty: Boolean = true,
        maxLength: Int? = null,

        inputType: Int = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,

        isCancelable: Boolean = true,
        isEnabled: Boolean = true,

        isPassword: Boolean = false,

        isCancelVisible: Boolean = true,
        validation: (text: String) -> Pair<Boolean, Int?> = { Pair(true, null) },
        errorText: String? = null,
        copyAction: ((text: String) -> Unit)? = null,
        cancelAction: () -> Unit = {},
        okAction: (text: String) -> Unit = {},
    ) {

        binding = DialogSimpleInputBinding.inflate(LayoutInflater.from(context))

        dialog = MaterialDialog(context)
            .cancelable(isCancelable)
            .customView(view = binding.root, noVerticalPadding = true, horizontalPadding = false)
            .onPreShow {
                binding.simpleInputEditText.post {
                    binding.simpleInputEditText.requestFocus()
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.simpleInputEditText, InputMethodManager.SHOW_IMPLICIT)
                }
            }

        title?.let { dialog.title(text = it) }

        dialog.show {
            if (preFill.isNullOrBlank() && allowEmpty.not()) {
                binding.simpleInputOk.isEnabled = false
            }

            dialog.setOnDismissListener {
            }

            description?.let { binding.simpleInputDescription.text = it }
            binding.simpleInputDescription.isVisible = description != null

            okText?.let { binding.simpleInputOk.text = it }
            binding.simpleInputOk.setOnClickListener {
                okAction.invoke(binding.simpleInputEditText.text.toString().trim())
                dismiss()
            }
            binding.simpleInputEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && binding.simpleInputOk.isEnabled) {
                    okAction.invoke(binding.simpleInputEditText.text.toString().trim())
                    dismiss()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
            binding.simpleInputCancel.setOnClickListener {
                cancelAction()
                dismiss()
            }
            binding.simpleInputLayout.hint = hint

            if (maxLength != null && maxLength > 0) {
                binding.simpleInputLayout.isCounterEnabled = true
                binding.simpleInputLayout.counterMaxLength = maxLength
            }

            binding.simpleInputEditText.inputType = inputType
            binding.simpleInputEditText.setText(preFill)
            binding.simpleInputEditText.setSelection(preFill?.length ?: 0)

            binding.simpleInputEditText.onTextChanged {
                val minLength = if (allowEmpty) 0 else 1
                binding.simpleInputOk.isEnabled = it.length in minLength..(maxLength ?: 999999) && validation.invoke(it).first
                binding.simpleInputLayout.error = validation.invoke(it).second?.let { context.getString(it) }
            }

            binding.simpleInputEditText.isEnabled = isEnabled

            if (isPassword) {
                binding.simpleInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            } else {
                binding.simpleInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
            }

            if (copyAction != null) {
                binding.simpleInputCopy.makeVisible()
                binding.simpleInputCopy.setOnClickListener {
                    copyAction.invoke(binding.simpleInputEditText.text.toString().trim())
                    dismiss()
                }

            } else {
                binding.simpleInputCopy.makeGone()
            }

            if (errorText.isNullOrBlank().not()) {
                binding.simpleInputLayout.error = errorText
            }

            binding.simpleInputCancel.isInvisible = isCancelVisible.not()
        }
    }

    fun dismiss() {
        if (::dialog.isInitialized) {
            dialog.dismiss()
        }
    }
}