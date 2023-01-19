package com.twofasapp.features.addserviceqr

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.twofasapp.databinding.ViewScanInfoDialogBinding
import com.twofasapp.extensions.dpToPx

class ScanInfoDialogView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val viewBinding = ViewScanInfoDialogBinding.inflate(LayoutInflater.from(context), this)

    init {
        setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        orientation = VERTICAL
    }

    fun setup(
        title: String,
        desc: String,
        descSpan: Spannable? = null,
        okText: String,
        imageRes: Int,
        showCancel: Boolean = true,
        okAction: () -> Unit,
        cancelAction: () -> Unit,
    ) {
        viewBinding.title.text = title
        viewBinding.desc.text = descSpan ?: desc
        viewBinding.confirm.text = okText
        viewBinding.image.setImageResource(imageRes)
        viewBinding.cancel.isVisible = showCancel
        viewBinding.confirm.setOnClickListener { okAction.invoke() }
        viewBinding.cancel.setOnClickListener { cancelAction.invoke() }
    }
}