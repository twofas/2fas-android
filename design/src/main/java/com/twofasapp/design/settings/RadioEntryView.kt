package com.twofasapp.design.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.twofasapp.design.databinding.ViewRadioEntryBinding
import com.twofasapp.extensions.dpToPx
import com.twofasapp.extensions.getDrawableFromAttr
import com.twofasapp.extensions.getString
import com.twofasapp.extensions.makeGoneIfEmpty
import com.twofasapp.extensions.makeInvisible
import com.twofasapp.extensions.makeVisible
import com.twofasapp.extensions.setTint
import com.twofasapp.extensions.setTintRes

class RadioEntryView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val viewBinding = ViewRadioEntryBinding.inflate(LayoutInflater.from(context), this)
    private var model: RadioEntry = RadioEntry()

    init {
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        background = ContextCompat.getDrawable(context, context.getDrawableFromAttr(android.R.attr.selectableItemBackground))
        minHeight = dpToPx(56)
    }

    fun update(action: (RadioEntry) -> RadioEntry) {
        model = action.invoke(model)
        updateView()
    }

    private fun updateView() {
        with(viewBinding) {
            entryTitle.text = root.context.getString(model.title, model.titleRes)
            entrySubtitle.text = root.context.getString(model.subtitle, model.subtitleRes)
            entrySubtitle.makeGoneIfEmpty()

            isEnabled = model.isEnabled
            entryTitle.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()
            entrySubtitle.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()
            entryIcon.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()

            if (model.drawableRes == null) {
                entryIcon.makeInvisible()
            } else {
                entryIcon.setImageResource(model.drawableRes!!)

                when {
                    model.drawableTint != null -> entryIcon.setTint(model.drawableTint!!)
                    model.drawableTintRes != null -> entryIcon.setTintRes(model.drawableTintRes!!)
                }

                entryIcon.makeVisible()
            }

            when (model.radioGravity) {
                RadioEntry.RadioGravity.START -> {
                    entryRadio.isVisible = false
                    entryRadioStart.isVisible = true
                    entryRadioStart.isClickable = false
                    entryRadioStart.isFocusable = false
                    entryRadioStart.isChecked = model.isChecked
                    setOnClickListener { model.toggleAction?.invoke(model, entryRadioStart.isChecked.not()) }
                }
                RadioEntry.RadioGravity.END -> {
                    entryRadio.isVisible = true
                    entryRadioStart.isVisible = false
                    entryRadio.isClickable = false
                    entryRadio.isFocusable = false
                    entryRadio.isChecked = model.isChecked
                    setOnClickListener { model.toggleAction?.invoke(model, entryRadio.isChecked.not()) }
                }
            }
        }
    }
}