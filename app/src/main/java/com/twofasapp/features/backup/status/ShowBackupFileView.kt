package com.twofasapp.features.backup.status

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.twofasapp.extensions.dpToPx
import com.twofasapp.databinding.ViewShowBackupFileBinding
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans.superscript

class ShowBackupFileView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val binding = ViewShowBackupFileBinding.inflate(LayoutInflater.from(context), this)

    init {
        setPadding(dpToPx(16), dpToPx(0), dpToPx(16), dpToPx(0))
        orientation = VERTICAL
    }

    fun setContent(text: String) {
        binding.content.text = Spanner()
            .append(text, superscript())
    }
}