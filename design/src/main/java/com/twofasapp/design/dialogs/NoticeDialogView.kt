package com.twofasapp.design.dialogs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.twofasapp.design.databinding.ViewNoticeDialogBinding
import com.twofasapp.extensions.dpToPx

class NoticeDialogView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val binding = ViewNoticeDialogBinding.inflate(LayoutInflater.from(context), this)

    init {
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        orientation = VERTICAL
    }

    fun setup(
        imageRes: Int,
        titleRes: Int,
        descRes: Int,
        positiveRes: Int,
        negativeRes: Int,
        isPositiveVisible: Boolean,
        isNegativeVisible: Boolean,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit,
        closeAction: () -> Unit
    ) {
        with(binding) {
            title.setText(titleRes)
            desc.setText(descRes)
            image.setImageResource(imageRes)

            positiveButton.setText(positiveRes)
            negativeButton.setText(negativeRes)

            positiveButton.isVisible = isPositiveVisible
            negativeButton.isVisible = isNegativeVisible

            positiveButton.setOnClickListener { positiveAction.invoke() }
            negativeButton.setOnClickListener { negativeAction.invoke() }
            close.setOnClickListener { closeAction.invoke() }
        }
    }
}