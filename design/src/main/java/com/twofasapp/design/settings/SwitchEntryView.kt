package com.twofasapp.design.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.twofasapp.design.R
import com.twofasapp.design.databinding.ViewSwitchEntryBinding
import com.twofasapp.extensions.*
import kotlin.with

class SwitchEntryView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val viewBinding = ViewSwitchEntryBinding.inflate(LayoutInflater.from(context), this)
    private var model: SwitchEntry = SwitchEntry()

    init {
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        background = ContextCompat.getDrawable(context, context.getDrawableFromAttr(android.R.attr.selectableItemBackground))
        minHeight = dpToPx(56)
    }

    fun update(action: (SwitchEntry) -> SwitchEntry) {
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

            entrySwitch.isClickable = false
            entrySwitch.isFocusable = false
            entrySwitch.isChecked = model.isChecked
            setOnClickListener { model.switchAction?.invoke(model, entrySwitch.isChecked.not()) }
        }
    }
}