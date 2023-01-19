package com.twofasapp.design.settings

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.twofasapp.resources.R
import com.twofasapp.extensions.dpToPx
import com.twofasapp.extensions.getString

class HeaderEntryView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var model: HeaderEntry = HeaderEntry()

    init {
        setPadding(dpToPx(72), dpToPx(16), dpToPx(16), dpToPx(12))
        setTextAppearance(R.style.Text_Body_Bold)
        setTextColor(Color.parseColor("#d81f26"))
    }

    fun update(action: (HeaderEntry) -> HeaderEntry) {
        model = action.invoke(model)
        updateView()
    }

    private fun updateView() {
        text = context.getString(model.text, model.textRes)
    }
}